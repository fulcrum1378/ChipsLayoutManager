package ir.mahdiparastesh.chlm.sample.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import ir.mahdiparastesh.chlm.sample.databinding.FragmentBottomSheetBinding;

public class BottomSheetFragment extends Fragment {
    FragmentBottomSheetBinding b;

    public BottomSheetFragment() {
    }

    public static BottomSheetFragment newInstance() {
        Bundle args = new Bundle();
        BottomSheetFragment fragment = new BottomSheetFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        b = FragmentBottomSheetBinding.inflate(inflater, container, false);
        return b.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        b.btnShowSheet.setOnClickListener(v -> {
            BottomSheetDialogFragment fragment = BottomSheetDialogFragment.newInstance();
            fragment.show(getChildFragmentManager(), fragment.getTag());
        });
    }
}
