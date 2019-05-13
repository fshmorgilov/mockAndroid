package com.themaker.fshmo.klassikaplus.data.web.catalog

import com.themaker.fshmo.klassikaplus.data.web.dto.revision.ResponseDto
import retrofit2.Call
import retrofit2.http.GET

interface RevisionEndpoint {

    @GET("revision")
    fun checkRevision(): Call<ResponseDto>
}