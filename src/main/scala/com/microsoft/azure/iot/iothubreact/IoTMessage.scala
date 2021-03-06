// Copyright (c) Microsoft. All rights reserved.

package com.microsoft.azure.iot.iothubreact

import java.time.Instant

import com.microsoft.azure.eventhubs.EventData

/* IoTMessage factory */
private object IoTMessage {

  /** Create a user friendly representation of the IoT message from the raw
    * data coming from the storage
    *
    * @param rawData   Raw data retrieved from the IoT hub storage
    * @param partition Storage partition where the message was retrieved from
    *
    * @return
    */
  def apply(rawData: EventData, partition: Option[Int]): IoTMessage = {
    new IoTMessage(rawData, partition)
  }
}

/** Expose the IoT message body and timestamp
  *
  * @param partition Storage partition where the message was retrieved from
  */
class IoTMessage(data: EventData, val partition: Option[Int]) {

  // Internal properties set by IoT stoage
  private[this] lazy val systemProps = data.getSystemProperties()

  /** Time when the message was received by IoT hub service
    * Note that this might differ from the time set by the device, e.g. in case
    * of batch uploads
    */
  lazy val created: Instant = systemProps.getEnqueuedTime

  /** IoT message offset
    * Useful for example to store the current position in the stream
    */
  lazy val offset: String = systemProps.getOffset

  // IoT message sequence number
  lazy val sequenceNumber: Long = systemProps.getSequenceNumber

  // ID of the device who sent the message
  lazy val deviceId: String = systemProps.get("iothub-connection-device-id").toString

  // IoT message content bytes
  lazy val content: Array[Byte] = data.getBody

  // IoT message content as string, e.g. JSON/XML/etc.
  lazy val contentAsString: String = new String(content)
}
