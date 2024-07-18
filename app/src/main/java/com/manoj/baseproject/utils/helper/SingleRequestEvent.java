package com.manoj.baseproject.utils.helper;


import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;

import com.manoj.baseproject.utils.helper.Resource;
import com.manoj.baseproject.utils.helper.SingleLiveEvent;


public class SingleRequestEvent<T> extends SingleLiveEvent<Resource<T>> {
    public interface RequestObserver<T> {
        void onRequestReceived(@NonNull Resource<T> resource);
    }

    public void observe(LifecycleOwner owner, final RequestObserver<T> observer) {
        super.observe(owner, resource -> {
            if (resource == null) {
                return;
            }

            observer.onRequestReceived(resource);
        });
    }

}
