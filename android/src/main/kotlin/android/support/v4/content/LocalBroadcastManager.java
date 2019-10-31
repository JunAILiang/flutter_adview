//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package android.support.v4.content;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class LocalBroadcastManager {
    private static final String TAG = "LocalBroadcastManager";
    private static final boolean DEBUG = false;
    private final Context mAppContext;
    private final HashMap<BroadcastReceiver, ArrayList<IntentFilter>> mReceivers = new HashMap();
    private final HashMap<String, ArrayList<LocalBroadcastManager.ReceiverRecord>> mActions = new HashMap();
    private final ArrayList<LocalBroadcastManager.BroadcastRecord> mPendingBroadcasts = new ArrayList();
    static final int MSG_EXEC_PENDING_BROADCASTS = 1;
    private final Handler mHandler;
    private static final Object mLock = new Object();
    private static LocalBroadcastManager mInstance;

    public static LocalBroadcastManager getInstance(Context context) {
        synchronized(mLock) {
            if (mInstance == null) {
                mInstance = new LocalBroadcastManager(context.getApplicationContext());
            }

            return mInstance;
        }
    }

    private LocalBroadcastManager(Context context) {
        this.mAppContext = context;
        this.mHandler = new Handler(context.getMainLooper()) {
            public void handleMessage(Message msg) {
                switch(msg.what) {
                    case 1:
                        LocalBroadcastManager.this.executePendingBroadcasts();
                        break;
                    default:
                        super.handleMessage(msg);
                }

            }
        };
    }

    public void registerReceiver(BroadcastReceiver receiver, IntentFilter filter) {
        synchronized(this.mReceivers) {
            LocalBroadcastManager.ReceiverRecord entry = new LocalBroadcastManager.ReceiverRecord(filter, receiver);
            ArrayList<IntentFilter> filters = (ArrayList)this.mReceivers.get(receiver);
            if (filters == null) {
                filters = new ArrayList(1);
                this.mReceivers.put(receiver, filters);
            }

            filters.add(filter);

            for(int i = 0; i < filter.countActions(); ++i) {
                String action = filter.getAction(i);
                ArrayList<LocalBroadcastManager.ReceiverRecord> entries = (ArrayList)this.mActions.get(action);
                if (entries == null) {
                    entries = new ArrayList(1);
                    this.mActions.put(action, entries);
                }

                entries.add(entry);
            }

        }
    }

    public void unregisterReceiver(BroadcastReceiver receiver) {
        synchronized(this.mReceivers) {
            ArrayList<IntentFilter> filters = (ArrayList)this.mReceivers.remove(receiver);
            if (filters != null) {
                for(int i = 0; i < filters.size(); ++i) {
                    IntentFilter filter = (IntentFilter)filters.get(i);

                    for(int j = 0; j < filter.countActions(); ++j) {
                        String action = filter.getAction(j);
                        ArrayList<LocalBroadcastManager.ReceiverRecord> receivers = (ArrayList)this.mActions.get(action);
                        if (receivers != null) {
                            for(int k = 0; k < receivers.size(); ++k) {
                                if (((LocalBroadcastManager.ReceiverRecord)receivers.get(k)).receiver == receiver) {
                                    receivers.remove(k);
                                    --k;
                                }
                            }

                            if (receivers.size() <= 0) {
                                this.mActions.remove(action);
                            }
                        }
                    }
                }

            }
        }
    }

    public boolean sendBroadcast(Intent intent) {
        synchronized(this.mReceivers) {
            String action = intent.getAction();
            String type = intent.resolveTypeIfNeeded(this.mAppContext.getContentResolver());
            Uri data = intent.getData();
            String scheme = intent.getScheme();
            Set<String> categories = intent.getCategories();
            boolean debug = (intent.getFlags() & 8) != 0;
            if (debug) {
                Log.v("LocalBroadcastManager", "Resolving type " + type + " scheme " + scheme + " of intent " + intent);
            }

            ArrayList<LocalBroadcastManager.ReceiverRecord> entries = (ArrayList)this.mActions.get(intent.getAction());
            if (entries != null) {
                if (debug) {
                    Log.v("LocalBroadcastManager", "Action list: " + entries);
                }

                ArrayList<LocalBroadcastManager.ReceiverRecord> receivers = null;

                int i;
                for(i = 0; i < entries.size(); ++i) {
                    LocalBroadcastManager.ReceiverRecord receiver = (LocalBroadcastManager.ReceiverRecord)entries.get(i);
                    if (debug) {
                        Log.v("LocalBroadcastManager", "Matching against filter " + receiver.filter);
                    }

                    if (receiver.broadcasting) {
                        if (debug) {
                            Log.v("LocalBroadcastManager", "  Filter's target already added");
                        }
                    } else {
                        int match = receiver.filter.match(action, type, scheme, data, categories, "LocalBroadcastManager");
                        if (match >= 0) {
                            if (debug) {
                                Log.v("LocalBroadcastManager", "  Filter matched!  match=0x" + Integer.toHexString(match));
                            }

                            if (receivers == null) {
                                receivers = new ArrayList();
                            }

                            receivers.add(receiver);
                            receiver.broadcasting = true;
                        } else if (debug) {
                            String reason;
                            switch(match) {
                                case -4:
                                    reason = "category";
                                    break;
                                case -3:
                                    reason = "action";
                                    break;
                                case -2:
                                    reason = "data";
                                    break;
                                case -1:
                                    reason = "type";
                                    break;
                                default:
                                    reason = "unknown reason";
                            }

                            Log.v("LocalBroadcastManager", "  Filter did not match: " + reason);
                        }
                    }
                }

                if (receivers != null) {
                    for(i = 0; i < receivers.size(); ++i) {
                        ((LocalBroadcastManager.ReceiverRecord)receivers.get(i)).broadcasting = false;
                    }

                    this.mPendingBroadcasts.add(new LocalBroadcastManager.BroadcastRecord(intent, receivers));
                    if (!this.mHandler.hasMessages(1)) {
                        this.mHandler.sendEmptyMessage(1);
                    }

                    return true;
                }
            }

            return false;
        }
    }

    public void sendBroadcastSync(Intent intent) {
        if (this.sendBroadcast(intent)) {
            this.executePendingBroadcasts();
        }

    }

    private void executePendingBroadcasts() {
        while(true) {
            LocalBroadcastManager.BroadcastRecord[] brs = null;
            synchronized(this.mReceivers) {
                int N = this.mPendingBroadcasts.size();
                if (N <= 0) {
                    return;
                }

                brs = new LocalBroadcastManager.BroadcastRecord[N];
                this.mPendingBroadcasts.toArray(brs);
                this.mPendingBroadcasts.clear();
            }

            for(int i = 0; i < brs.length; ++i) {
                LocalBroadcastManager.BroadcastRecord br = brs[i];

                for(int j = 0; j < br.receivers.size(); ++j) {
                    ((LocalBroadcastManager.ReceiverRecord)br.receivers.get(j)).receiver.onReceive(this.mAppContext, br.intent);
                }
            }
        }
    }

    private static class BroadcastRecord {
        final Intent intent;
        final ArrayList<LocalBroadcastManager.ReceiverRecord> receivers;

        BroadcastRecord(Intent _intent, ArrayList<LocalBroadcastManager.ReceiverRecord> _receivers) {
            this.intent = _intent;
            this.receivers = _receivers;
        }
    }

    private static class ReceiverRecord {
        final IntentFilter filter;
        final BroadcastReceiver receiver;
        boolean broadcasting;

        ReceiverRecord(IntentFilter _filter, BroadcastReceiver _receiver) {
            this.filter = _filter;
            this.receiver = _receiver;
        }

        public String toString() {
            StringBuilder builder = new StringBuilder(128);
            builder.append("Receiver{");
            builder.append(this.receiver);
            builder.append(" filter=");
            builder.append(this.filter);
            builder.append("}");
            return builder.toString();
        }
    }
}
