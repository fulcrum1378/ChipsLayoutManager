package ir.mahdiparastesh.chlm.sample.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import ir.mahdiparastesh.chlm.sample.R;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BottomSheetFragment extends Fragment {

    public BottomSheetFragment() {
    }

    public static BottomSheetFragment newInstance() {
        Bundle args = new Bundle();
        BottomSheetFragment fragment = new BottomSheetFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_bottom_sheet, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
    }

    @OnClick(R.id.btnShowSheet)
    void onShowSheetClicked(View view) {
        BottomSheetDialogFragment fragment = BottomSheetDialogFragment.newInstance();
        fragment.show(getChildFragmentManager(), fragment.getTag());
    }
}
