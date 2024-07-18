
package com.manoj.baseproject.utils.helper;


import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

public class SingleMessageEvent extends MutableLiveData<Object> {
    public interface MessageObserver {
        void onMessageReceived(@StringRes int msgResId);

        void onMessageReceived(String msg);
    }

    public void observe(LifecycleOwner owner, final MessageObserver observer) {
        super.observe(owner, new Observer<Object>() {
            @Override
            public void onChanged(@Nullable Object t) {
                try {
                    if (t == null) {
                        return;
                    }
                    if (t instanceof String)
                        observer.onMessageReceived((String) t);
                    else if (t instanceof Integer)
                        observer.onMessageReceived((Integer) t);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
    }

}
