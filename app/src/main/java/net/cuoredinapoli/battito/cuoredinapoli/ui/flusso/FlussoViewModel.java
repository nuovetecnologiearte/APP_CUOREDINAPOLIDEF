package net.cuoredinapoli.battito.cuoredinapoli.ui.flusso;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class FlussoViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public FlussoViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("questa è la sezione flusso");
    }

    public LiveData<String> getText() {
        return mText;
    }
}