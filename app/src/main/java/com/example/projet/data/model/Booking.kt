package com.example.projet.data.model

import com.google.gson.TypeAdapter
import com.google.gson.annotations.JsonAdapter
import com.google.gson.annotations.SerializedName
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter

data class Booking(
    @SerializedName("_id") val id: String,
    val date: String?,
    val status: String?,
    
    @JsonAdapter(BookingCustomerAdapter::class)
    val customerId: BookingCustomer?,
    
    @JsonAdapter(BookingServiceAdapter::class)
    val serviceId: BookingService?,
    
    val providerId: String?
)

data class BookingCustomer(
    @SerializedName("_id") val id: String,
    val name: String? = null
)

data class BookingService(
    @SerializedName("_id") val id: String,
    val title: String? = null
)

// Custom Adapter for BookingCustomer
class BookingCustomerAdapter : TypeAdapter<BookingCustomer>() {
    override fun write(out: JsonWriter, value: BookingCustomer?) {
        if (value == null) {
            out.nullValue()
            return
        }
        out.value(value.id)
    }

    override fun read(reader: JsonReader): BookingCustomer? {
        val token = reader.peek()
        if (token == JsonToken.NULL) {
            reader.nextNull()
            return null
        }
        if (token == JsonToken.STRING) {
            val id = reader.nextString()
            return BookingCustomer(id = id, name = null)
        } else if (token == JsonToken.BEGIN_OBJECT) {
            reader.beginObject()
            var id = ""
            var name: String? = null
            
            while (reader.hasNext()) {
                val fieldName = reader.nextName()
                if (fieldName == "_id") {
                    id = reader.nextString()
                } else if (fieldName == "name") {
                    name = reader.nextString()
                } else {
                    reader.skipValue()
                }
            }
            reader.endObject()
            return BookingCustomer(id = id, name = name)
        }
        throw IllegalStateException("Unexpected token for customerId: $token")
    }
}

// Custom Adapter for BookingService
class BookingServiceAdapter : TypeAdapter<BookingService>() {
    override fun write(out: JsonWriter, value: BookingService?) {
        if (value == null) {
            out.nullValue()
            return
        }
        out.value(value.id)
    }

    override fun read(reader: JsonReader): BookingService? {
        val token = reader.peek()
        if (token == JsonToken.NULL) {
            reader.nextNull()
            return null
        }
        if (token == JsonToken.STRING) {
            val id = reader.nextString()
            return BookingService(id = id, title = null)
        } else if (token == JsonToken.BEGIN_OBJECT) {
            reader.beginObject()
            var id = ""
            var title: String? = null
            
            while (reader.hasNext()) {
                val fieldName = reader.nextName()
                if (fieldName == "_id") {
                    id = reader.nextString()
                } else if (fieldName == "title") {
                    title = reader.nextString()
                } else {
                    reader.skipValue()
                }
            }
            reader.endObject()
            return BookingService(id = id, title = title)
        }
        throw IllegalStateException("Unexpected token for serviceId: $token")
    }
}
