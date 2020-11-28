package com.gallosalocin.go4lunch.ui;

import com.gallosalocin.go4lunch.models.Workmate;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class MainActivityTest {

    private List<String> like = new ArrayList<>();

    @Mock
    FirebaseUser authTest;
    @Mock
    DocumentReference documentReferenceTest;

    @Before
    public void setUp() {
        authTest = mock(FirebaseUser.class);
        when(authTest.getUid()).thenReturn("uid test");
        when(authTest.getDisplayName()).thenReturn("name test");

        documentReferenceTest = mock(DocumentReference.class);
        when(documentReferenceTest.getId()).thenReturn("uid test");
    }

    @Test
    public void authenticationName_WorkmateName_ReturnSameName() {
        Workmate workmateTest = new Workmate(authTest.getDisplayName(), "photo test", "", "", like);
        assertThat(authTest.getDisplayName()).isEqualTo(workmateTest.getName());
    }

    @Test
    public void authenticationUid_DocumentReference_ReturnSameId() {
        assertThat(authTest.getUid()).isEqualTo(documentReferenceTest.getId());
    }
}