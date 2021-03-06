package org.sackfix.boostrap

import java.time.LocalDateTime

import akka.actor.ActorRef
import org.sackfix.common.message.SfMessage
import org.sackfix.common.validated.fields.SfFixMessageBody
import org.sackfix.session.SfSessionId

/**
  * Created by Jonathan during 2017.
  *
  * When you create the session hub actor you must inject your own businessmessage handler.
  * This is only called when the message has been fully validated and determined not to be a session
  * level message.
  *
  * I would suggest that this class has a reference to your business Actor, and forwards teh fix message
  * to the actor.  You can look after your own ActorRef and lifecycle.
  */
trait SfBusinessFixInfo {
  //val tstamp = LocalDateTime.now() //Commenting out as appears to be unused.

  def sessionId: SfSessionId
}

/**
  * @param sessionId      Holds details of the comp id's and so on
  * @param sfSessionActor If you want to send the session a message you use ! BusinessFixMsgOut()
  * @param message        Has .header, .body and tail
  */
case class BusinessFixMessage(sessionId: SfSessionId, sfSessionActor: ActorRef, message: SfMessage) extends SfBusinessFixInfo

/**
  * The other side rejected a message - either a session level or a business level message.  You should
  * think about what to do - humans should go have a look at the very least, or maybe disconnect and wait for humans?
  *
  * @param sessionId      Holds details of the comp id's and so on
  * @param sfSessionActor If you want to send the session a message you use ! BusinessFixMsgOut()
  * @param message        - the body will be a reject ie
  *                   message.body match{
  *                       case rj:RejectMessage =>
  *                       }
  */
case class BusinessRejectMessage(sessionId: SfSessionId, sfSessionActor: ActorRef, message: SfMessage) extends SfBusinessFixInfo


/**
  * @param sessionId      Holds details of the comp id's and so on
  * @param sfSessionActor If you want to send the session a message you use ! BusinessFixMsgOut()
  */
case class FixSessionOpen(sessionId: SfSessionId, sfSessionActor: ActorRef) extends SfBusinessFixInfo

/**
  * You may receive this more than once during close down.
  *
  * @param sessionId Holds details of the comp id's and so on
  */
case class FixSessionClosed(sessionId: SfSessionId) extends SfBusinessFixInfo

/**
  * The business layer uses this message to send a fix message out to the counterparty.  It should be sent
  * as an akka message to the sfSessionActor.
  *
  * With SackFix all TCP is ACK'ed, so you will recieve back the BusinessFixMsgOutAck when the message
  * has been sent.
  *
  * @param msgBody The message to be sent out
  */
case class BusinessFixMsgOut(msgBody: SfFixMessageBody, correlationId: String)

case class BusinessFixMsgOutAck(sessionId: SfSessionId, sfSessionActor: ActorRef, correlationId: String) extends SfBusinessFixInfo

/**
  * You can send this to the session Actor to tell it to close the session
  * @param reason This will be sent in the logout message to the counterparty, so make it polite!
  */
case class BusinessSaysLogoutNow(reason:String)

trait BusinessCommsHandler {
  def handleFix(msg: SfBusinessFixInfo)
}

