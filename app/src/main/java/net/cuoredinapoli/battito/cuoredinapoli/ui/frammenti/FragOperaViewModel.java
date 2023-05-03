package net.cuoredinapoli.battito.cuoredinapoli.ui.frammenti;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class FragOperaViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public FragOperaViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("");
    }

    public LiveData<String> getText() {
        return mText;
    }


}
