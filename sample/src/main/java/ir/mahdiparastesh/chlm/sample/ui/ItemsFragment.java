package ir.mahdiparastesh.chlm.sample.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import ir.mahdiparastesh.chlm.ChipsLayoutManager;
import ir.mahdiparastesh.chlm.SpacingItemDecoration;
import ir.mahdiparastesh.chlm.sample.R;
import ir.mahdiparastesh.chlm.sample.databinding.FragmentItemsBinding;

public class ItemsFragment extends Fragment {
    FragmentItemsBinding b;
    private static final String EXTRA = "data";

    private RecyclerView.Adapter<? extends RecyclerView.ViewHolder> adapter;
    private List<String> positions;
    private List items;

    private final IItemsFactory itemsFactory = new ShortChipsFactory();

    public ItemsFragment() {
        // Required empty public constructor
    }

    public static ItemsFragment newInstance() {
        return new ItemsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        b = FragmentItemsBinding.inflate(inflater, container, false);
        return b.getRoot();
    }

    private RecyclerView.Adapter<? extends RecyclerView.ViewHolder> createAdapter(
            Bundle savedInstanceState) {
        List<String> items;
        if (savedInstanceState == null)
            items = itemsFactory.getItems();
        else
            items = savedInstanceState.getStringArrayList(EXTRA);

        adapter = itemsFactory.createAdapter(items, onRemoveListener);
        this.items = items;
        return adapter;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        b.btnRevert.setOnClickListener(v -> {
            int position = b.spinnerPosition.getSelectedItemPosition();
            if (position == Spinner.INVALID_POSITION)
                return;

            int positionMoveTo = b.spinnerMoveTo.getSelectedItemPosition();
            if (positionMoveTo == Spinner.INVALID_POSITION)
                return;

            if (position == positionMoveTo) return;

            b.spinnerPosition.setSelection(positionMoveTo);
            b.spinnerMoveTo.setSelection(position);
        });

        b.btnDelete.setOnClickListener(v -> {
            int position = b.spinnerPosition.getSelectedItemPosition();
            if (position == Spinner.INVALID_POSITION)
                return;
            items.remove(position);
            adapter.notifyItemRemoved(position);
            updateSpinners();
        });

        b.btnMove.setOnClickListener(v -> {
            int position = b.spinnerPosition.getSelectedItemPosition();
            if (position == Spinner.INVALID_POSITION)
                return;

            int positionMoveTo = b.spinnerMoveTo.getSelectedItemPosition();
            if (positionMoveTo == Spinner.INVALID_POSITION)
                return;

            if (position == positionMoveTo) return;

            Object item = items.remove(position);
            items.add(positionMoveTo, item);

            adapter.notifyItemMoved(position, positionMoveTo);
        });

        b.btnScroll.setOnClickListener(
                v -> b.rvTest.scrollToPosition(b.spinnerPosition.getSelectedItemPosition()));

        b.btnInsert.setOnClickListener(v -> {
            int position = b.spinnerPosition.getSelectedItemPosition();
            if (position == Spinner.INVALID_POSITION)
                position = 0;
            items.add(position, itemsFactory.createOneItemForPosition(position));
            adapter.notifyItemInserted(position);
            updateSpinners();
        });

        adapter = createAdapter(savedInstanceState);

        ChipsLayoutManager spanLayoutManager = ChipsLayoutManager.newBuilder(getContext())
                .setOrientation(ChipsLayoutManager.HORIZONTAL)
                .build();

        b.rvTest.addItemDecoration(new SpacingItemDecoration(
                getResources().getDimensionPixelOffset(R.dimen.item_space),
                getResources().getDimensionPixelOffset(R.dimen.item_space)));

        positions = new LinkedList<>();
        for (int i = 0; i < items.size(); i++)
            positions.add(String.valueOf(i));

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_list_item_1, android.R.id.text1, positions);
        ArrayAdapter<String> spinnerAdapterMoveTo = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_list_item_1, android.R.id.text1, positions);
        b.spinnerPosition.setAdapter(spinnerAdapter);
        b.spinnerMoveTo.setAdapter(spinnerAdapterMoveTo);

        b.rvTest.setLayoutManager(spanLayoutManager);
        b.rvTest.getRecycledViewPool().setMaxRecycledViews(0, 10);
        b.rvTest.getRecycledViewPool().setMaxRecycledViews(1, 10);
        b.rvTest.setAdapter(adapter);

    }

    private final OnRemoveListener onRemoveListener = new OnRemoveListener() {
        @Override
        public void onItemRemoved(int position) {
            items.remove(position);
            adapter.notifyItemRemoved(position);
            updateSpinners();
        }
    };

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(EXTRA, new ArrayList<>(items));
    }

    private void updateSpinners() {
        positions = new LinkedList<>();
        for (int i = 0; i < items.size(); i++)
            positions.add(String.valueOf(i));

        int selectedPosition =
                Math.min(b.spinnerPosition.getSelectedItemPosition(), positions.size() - 1);
        int selectedMoveToPosition =
                Math.min(b.spinnerMoveTo.getSelectedItemPosition(), positions.size() - 1);

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_list_item_1, android.R.id.text1, positions);
        b.spinnerPosition.setAdapter(spinnerAdapter);
        selectedPosition = Math.min(spinnerAdapter.getCount() - 1, selectedPosition);
        b.spinnerPosition.setSelection(selectedPosition);

        ArrayAdapter<String> spinnerAdapterMoveTo = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_list_item_1, android.R.id.text1, positions);
        b.spinnerMoveTo.setAdapter(spinnerAdapterMoveTo);
        b.spinnerMoveTo.setSelection(selectedMoveToPosition);
    }
}
