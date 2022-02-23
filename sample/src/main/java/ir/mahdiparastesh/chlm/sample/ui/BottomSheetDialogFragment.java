package ir.mahdiparastesh.chlm.sample.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ir.mahdiparastesh.chlm.ChipsLayoutManager;
import ir.mahdiparastesh.chlm.SpacingItemDecoration;
import ir.mahdiparastesh.chlm.sample.R;
import ir.mahdiparastesh.chlm.sample.entity.ChipsEntity;

public class BottomSheetDialogFragment extends
        com.google.android.material.bottomsheet.BottomSheetDialogFragment {

    @BindView(R.id.rvBottomSheet)
    RecyclerView rvBottomSheet;

    private final IItemsFactory<ChipsEntity> itemsFactory = new ChipsFactory();

    public static BottomSheetDialogFragment newInstance() {
        Bundle args = new Bundle();
        BottomSheetDialogFragment fragment = new BottomSheetDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bottom_sheet_modal, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    private RecyclerView.Adapter<? extends RecyclerView.ViewHolder> createAdapter() {
        List<ChipsEntity> items = itemsFactory.getItems();
        return itemsFactory.createAdapter(items, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ChipsLayoutManager layoutManager = ChipsLayoutManager.newBuilder(getContext()).build();
        rvBottomSheet.setLayoutManager(layoutManager);
        rvBottomSheet.setAdapter(createAdapter());
        rvBottomSheet.addItemDecoration(new SpacingItemDecoration(
                getResources().getDimensionPixelOffset(R.dimen.item_space),
                getResources().getDimensionPixelOffset(R.dimen.item_space)));
    }
}
