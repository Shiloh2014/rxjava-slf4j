package com.github.davidmoten.rx.slf4j;

import static com.github.davidmoten.rx.slf4j.Logging.log;
import rx.Observable.Operator;
import rx.Observer;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Action0;
import rx.observers.Subscribers;
import rx.subjects.PublishSubject;
import rx.subscriptions.Subscriptions;

import com.github.davidmoten.rx.slf4j.Logging.Parameters;
import com.github.davidmoten.rx.slf4j.Logging.Parameters.Message;

public class OperatorLogging<T> implements Operator<T, T> {

	private final Parameters<T> p;

	public OperatorLogging(Parameters<T> parameters) {
		this.p = parameters;
	}

	@Override
	public Subscriber<? super T> call(Subscriber<? super T> child) {
		Subscription listener = Subscriptions.create(new Action0() {

			@Override
			public void call() {
				if (p.getUnsubscribedMessage() != null)
					log(p.getLogger(), p.getUnsubscribedMessage(),
							p.getUnsubscribedLevel(), null);
			}

		});
		child.add(listener);
		Subscriber<T> parent = Subscribers.from(createObserver(p.getSubject()));
		child.add(parent);

		Observer<Message<T>> observer = new Observer<Message<T>>() {

			@Override
			public void onCompleted() {
			}

			@Override
			public void onError(Throwable e) {
			}

			@Override
			public void onNext(Message<T> t) {
			}
		};
		Subscription sub = p.getObservable().subscribe(observer);
		child.add(sub);

		if (p.getSubscribedMessage() != null)
			log(p.getLogger(), p.getSubscribedMessage(),
					p.getSubscribedLevel(), null);
		return parent;
	}

	static <T> Observer<T> createObserver(final PublishSubject<T> subject) {
		return new Observer<T>() {

			@Override
			public void onCompleted() {
				subject.onCompleted();
			}

			@Override
			public void onError(Throwable e) {
				subject.onError(e);
			}

			@Override
			public void onNext(T t) {
				subject.onNext(t);
			}
		};
	}

}
