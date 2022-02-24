package ir.mahdiparastesh.chlm.sample.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ir.mahdiparastesh.chlm.ChipsLayoutManager;
import ir.mahdiparastesh.chlm.SpacingItemDecoration;
import ir.mahdiparastesh.chlm.sample.R;
import ir.mahdiparastesh.chlm.sample.databinding.FragmentBottomSheetModalBinding;
import ir.mahdiparastesh.chlm.sample.entity.ChipsEntity;

public class BottomSheetDialogFragment extends
        com.google.android.material.bottomsheet.BottomSheetDialogFragment {
    FragmentBottomSheetModalBinding b;
    private final IItemsFactory<ChipsEntity> itemsFactory = new ChipsFactory();

    public static BottomSheetDialogFragment newInstance() {
        Bundle args = new Bundle();
        BottomSheetDialogFragment fragment = new BottomSheetDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        b = FragmentBottomSheetModalBinding.inflate(inflater, container, false);
        return b.getRoot();
    }

    private RecyclerView.Adapter<? extends RecyclerView.ViewHolder> createAdapter() {
        List<ChipsEntity> items = itemsFactory.getItems();
        return itemsFactory.createAdapter(items, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ChipsLayoutManager layoutManager = ChipsLayoutManager.newBuilder(getContext()).build();
        b.rvBottomSheet.setLayoutManager(layoutManager);
        b.rvBottomSheet.setAdapter(createAdapter());
        b.rvBottomSheet.addItemDecoration(new SpacingItemDecoration(
                getResources().getDimensionPixelOffset(R.dimen.item_space),
                getResources().getDimensionPixelOffset(R.dimen.item_space)));
    }
}
