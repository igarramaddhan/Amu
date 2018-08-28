package com.ramz.igar.amu.model;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.BehaviorSubject;

public class ItemSubscriber <T>{
    private BehaviorSubject<T> behaviorSubject;

    public ItemSubscriber(BehaviorSubject<T> behaviorSubject){
        this.behaviorSubject = behaviorSubject;
    }

    public void subscibe(final OnNext<T> onNext){
        behaviorSubject.subscribe(new Observer<T>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(T value) {
                onNext.onNextValue(value);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }

    public interface OnNext<T> {
        void onNextValue(T value);
    }
}
