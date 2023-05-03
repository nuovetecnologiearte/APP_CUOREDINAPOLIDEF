package net.cuoredinapoli.battito.cuoredinapoli.ui.concept;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ConceptViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public ConceptViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("");
    }


    public LiveData<String> getText() {
        return mText;
    }
}