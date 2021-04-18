package com.gallosalocin.go4lunch.ui

import com.gallosalocin.go4lunch.models.Workmate
import com.google.common.truth.Truth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner
import java.util.*

@RunWith(MockitoJUnitRunner::class)
class MainActivityTest {

    private val like: List<String> = ArrayList()

    @Mock
    lateinit var authTest: FirebaseUser

    @Mock
    lateinit var documentReferenceTest: DocumentReference

    @Before
    fun setUp() {
        authTest = Mockito.mock(FirebaseUser::class.java)
        Mockito.`when`(authTest.uid).thenReturn("uid test")
        Mockito.`when`(authTest.displayName).thenReturn("name test")
        documentReferenceTest = Mockito.mock(DocumentReference::class.java)
        Mockito.`when`(documentReferenceTest.id).thenReturn("uid test")
    }

    @Test
    fun authenticationName_WorkmateName_ReturnSameName() {
        val (name) = Workmate(authTest.displayName, "photo test", "", "", like)
        Truth.assertThat(authTest.displayName).isEqualTo(name)
    }

    @Test
    fun authenticationUid_DocumentReference_ReturnSameId() {
        Truth.assertThat(authTest.uid).isEqualTo(documentReferenceTest.id)
    }
}