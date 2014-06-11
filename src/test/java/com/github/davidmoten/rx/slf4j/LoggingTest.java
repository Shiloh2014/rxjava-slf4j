package com.github.davidmoten.rx.slf4j;

import static com.github.davidmoten.rx.slf4j.Logging.logger;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import rx.Observable;
import rx.functions.Func1;

import com.github.davidmoten.rx.slf4j.Logging.Level;

public class LoggingTest {

	@Test
	public void testName() {
		int count = Observable.range(1, 10)
		// log all
				.lift(logger(LoggingTest.class).showValue().log())
				// count
				.count().toBlocking().single();
		assertEquals(10, count);
	}

	@Test
	public void testCountEvery() {
		int count = Observable
				.range(1, 10)
				// log all
				.lift(logger().name("rx.Server").prefix("count every test")
						.excludeValue().subscribed(Level.DEBUG)
						.onCompleted(Level.DEBUG).showCount("files").every(2)
						.log())
				// count
				.count().toBlocking().single();
		assertEquals(10, count);
	}

	@Test
	public void testAgain() {
		int count = Observable
				.range(51, 10)
				// log all
				.lift(logger().showCount().start(2).finish(2).showStackTrace()
						.showMemory().log())
				// count
				.count().toBlocking().single();
		assertEquals(10, count);
	}

	@Test
	public void testSubscribe() {
		int count = Observable
				.range(11, 3010)
				.lift(logger().showValue().showCount().every(1000).showMemory()
						.log()).count().toBlocking().single();
		assertEquals(3010, count);

	}

	@Test
	public void testCallingClass() {

		assertEquals(10, new CallingClass().count());
	}

	private static class CallingClass {
		public int count() {
			return Observable.range(1, 10)
			// log all
					.lift(logger().showValue().log())
					// count
					.count().toBlocking().single();
		}
	}

	@Test
	public void testKitchenSink() {
		Observable.range(1, 100)
		// log
				.lift(Logging.<Integer> logger("Boo")
				// count
						.showCount()
						// start on 2nd item
						.start(2)
						// ignore after 8th item
						.finish(18)
						// take every third item
						.every(3)
						// set the onCompleted message
						.onCompleted("finished")
						// at logging level
						.onCompleted(Level.INFO)
						// set the error logging level
						.onError(Level.WARN)
						// onNext at debug level
						.onNext(Level.DEBUG)
						// how to format the onNext item
						.onNextFormat("time=%sdays")
						// show onNext items
						.showValue()
						// show subscribed message at INFO level
						.subscribed(Level.INFO)
						// the message to show at subscription time
						.subscribed("created subscription")
						// the unsubscribe message at DEBUG level
						.unsubscribed(Level.DEBUG)
						// the unsubscribe message
						.unsubscribed("ended subscription")
						// only when item is an even number
						.when(new Func1<Integer, Boolean>() {
							@Override
							public Boolean call(Integer n) {
								return n % 2 == 0;
							}
						})
						// count those items passing the filters above
						.showCount("finalCount")
						// build the operator
						.log())
				// block and get the answer
				.toBlocking().last();
	}
}
