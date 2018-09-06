package org.sackfix.session.heartbeat

import java.time.temporal.ChronoUnit
import java.time.{LocalDateTime, LocalTime}

import org.scalatest.FlatSpec
import org.slf4j.LoggerFactory

/**
  * Created by Jonathan during 2017.
  */
class SfSessionSchedulerSpec extends FlatSpec {
  private val logger = LoggerFactory.getLogger(SfSessionSchedulerSpec.super.getClass)
  behavior of "SfSessionScheduler"

  it should "Fire wakeUp once as expected" in {
    val now = LocalTime.now

    val start = now.truncatedTo(ChronoUnit.DAYS).plusSeconds(1)
    val end = now.truncatedTo(ChronoUnit.DAYS).minusSeconds(1) //Test could still fail if run very close to midnight

    val l = new StubWakeUpListener
    val sched = SfSessionScheduler(start, end, l)
    sched.heartBeatFired()
    assert(l.wakeUpCallCount == 1)
    assert(l.sleepNowCallCount == 0)

    // Should remain unchanged
    sched.heartBeatFired()
    assert(l.wakeUpCallCount == 1)
    assert(l.sleepNowCallCount == 0)
  }

  it should "Fire sleepNow once as expected" in {
    val now = LocalTime.now
    val end = now.truncatedTo(ChronoUnit.DAYS).plusSeconds(1)
    val start = now.truncatedTo(ChronoUnit.DAYS).minusSeconds(1) //Test could still fail if run very close to midnight

    val l = new StubWakeUpListener
    val sched = SfSessionScheduler(start, end, l)
    sched.heartBeatFired()
    assert(l.wakeUpCallCount == 0)
    assert(l.sleepNowCallCount == 1)

    // Should remain unchanged
    sched.heartBeatFired()
    assert(l.wakeUpCallCount == 0)
    assert(l.sleepNowCallCount == 1)
  }
  it should "Wake up and then sleep" in {
    val now = LocalTime.now
    val start = now.plusNanos(200 * 1000000) // 200ms
    val end = now.plusNanos(300 * 1000000)

    val l = new StubWakeUpListener
    val sched = SfSessionScheduler(start, end, l)
    for (i <- 0 until 50) {
      Thread.sleep(300/50)
      sched.heartBeatFired()
      if (i>250) {
        assert(l.wakeUpCallCount == 1)
      }
    }
    assert(l.sleepNowCallCount == 2)
  }


}

class StubWakeUpListener extends SfSessionSchedulListener {
  private val logger = LoggerFactory.getLogger(StubWakeUpListener.super.getClass)
  var wakeUpCallCount = 0
  var sleepNowCallCount = 0

  override def wakeUp(): Unit = {
    wakeUpCallCount += 1
  }

  override def sleepNow(): Unit = {
    sleepNowCallCount += 1
  }
}



