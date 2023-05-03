package net.cuoredinapoli.battito.cuoredinapoli.ui.battito;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class BattitoViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public BattitoViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Battito");
    }

    public LiveData<String> getText() {
        return mText;
    }
}