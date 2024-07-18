package com.manoj.baseproject.utils.helper;

/**
 * Status of a resource that is provided to the UI.
 * <p>
 * These are usually created by the Repository classes where they return
 * {@code LiveData<Resource<T>>} to pass base_back the latest slot_list to the UI with its fetch status.
 */
public enum Status {
    SUCCESS,
    ERROR,
    LOADING,WARN
}
