package com.example.projet.data.model

import com.google.gson.TypeAdapter
import com.google.gson.annotations.JsonAdapter
import com.google.gson.annotations.SerializedName
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter

data class Service(
    @SerializedName("_id") val id: String,
    val title: String,
    val description: String,
    val price: Double,
    val photoURL: String?,
    
    @JsonAdapter(ServiceProviderAdapter::class)
    val providerId: ServiceProvider,
    
    val categoryId: String // Keeping this simple for now
)

data class ServiceProvider(
    @SerializedName("_id") val id: String,
    val name: String? = null
)

// Custom Adapter to handle both String and Object for providerId
class ServiceProviderAdapter : TypeAdapter<ServiceProvider>() {
    override fun write(out: JsonWriter, value: ServiceProvider?) {
        if (value == null) {
            out.nullValue()
            return
        }
        // When writing back, decide if you want to send just ID or object.
        // Usually for requests we send ID, but for this use case (reading), we just need to read correctly.
        // Here we'll write the object to be safe, or just the ID string if your backend expects that.
        // For simplicity, let's write the ID string which is safer for most "create/update" requests if they used this model.
        out.value(value.id)
    }

    override fun read(reader: JsonReader): ServiceProvider {
        val token = reader.peek()
        if (token == JsonToken.STRING) {
            val id = reader.nextString()
            return ServiceProvider(id = id, name = null)
        } else if (token == JsonToken.BEGIN_OBJECT) {
            reader.beginObject()
            var id = ""
            var name: String? = null
            
            while (reader.hasNext()) {
                when (reader.nextName()) {
                    "_id" -> id = reader.nextString()
                    "name" -> name = reader.nextString()
                    else -> reader.skipValue()
                }
            }
            reader.endObject()
            return ServiceProvider(id = id, name = name)
        }
        throw IllegalStateException("Unexpected token for providerId: $token")
    }
}
