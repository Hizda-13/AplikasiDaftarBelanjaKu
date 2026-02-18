package com.ryanjuniarto.daftarbelanjaku;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    private EditText editName, editQuantity;
    private RadioGroup radioCategory, radioPriority;
    private Button btnAdd, btnPickDate;
    private RecyclerView recyclerView;
    private ItemAdapter adapter;
    private ArrayList<Item> itemList;
    private DatabaseHelper dbHelper;
    private String selectedDate = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize database
        dbHelper = new DatabaseHelper(this);

        // Initialize views
        initViews();

        // Setup RecyclerView
        setupRecyclerView();

        // Load data from database
        loadItems();

        // Date picker button
        btnPickDate.setOnClickListener(v -> showDatePicker());

        // Add button click
        btnAdd.setOnClickListener(v -> addNewItem());
    }

    private void initViews() {
        editName = findViewById(R.id.edit_name);
        editQuantity = findViewById(R.id.edit_quantity);
        radioCategory = findViewById(R.id.radio_category);
        radioPriority = findViewById(R.id.radio_priority);
        btnAdd = findViewById(R.id.btn_add);
        btnPickDate = findViewById(R.id.btn_pick_date);
        recyclerView = findViewById(R.id.recycler_view);
    }

    private void setupRecyclerView() {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        itemList = new ArrayList<>();
        adapter = new ItemAdapter(itemList);
        recyclerView.setAdapter(adapter);

        // Set item click listener
        adapter.setOnItemClickListener(new ItemAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Item item = itemList.get(position);
                showItemDetails(item);
            }

            @Override
            public void onCheckClick(int position, boolean isChecked) {
                Item item = itemList.get(position);
                item.setIsChecked(isChecked);
                dbHelper.toggleCheck(item.getId(), isChecked);
                adapter.notifyItemChanged(position);
            }

            @Override
            public void onEditClick(int position) {
                Item item = itemList.get(position);
                showEditDialog(item, position);
            }

            @Override
            public void onDeleteClick(int position) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Delete Item")
                        .setMessage("Are you sure you want to delete this item?")
                        .setPositiveButton("Delete", (dialog, which) -> {
                            Item item = itemList.get(position);
                            dbHelper.deleteItem(item.getId());
                            itemList.remove(position);
                            adapter.notifyItemRemoved(position);
                            Toast.makeText(MainActivity.this, "Item deleted", Toast.LENGTH_SHORT).show();
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            }
        });
    }

    private void loadItems() {
        itemList.clear();
        itemList.addAll(dbHelper.getAllItems());
        adapter.notifyDataSetChanged();
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePicker = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        selectedDate = day + "/" + (month + 1) + "/" + year;
                        btnPickDate.setText(selectedDate);
                    }
                }, year, month, day);
        datePicker.show();
    }

    private void addNewItem() {
        String name = editName.getText().toString().trim();
        String quantity = editQuantity.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            editName.setError("Item name is required");
            return;
        }

        if (TextUtils.isEmpty(selectedDate)) {
            Toast.makeText(this, "Please select a date", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get selected category - DEFAULT KE "Household" jika tidak dipilih
        int categoryId = radioCategory.getCheckedRadioButtonId();
        String category = "Household"; // DEFAULT KE HOUSEHOLD
        if (categoryId != -1) {
            RadioButton radioButton = findViewById(categoryId);
            category = radioButton.getText().toString();
        }

        // Get selected priority
        int priorityId = radioPriority.getCheckedRadioButtonId();
        int priority = 1; // Default low
        if (priorityId != -1) {
            if (priorityId == R.id.radio_medium) priority = 2;
            else if (priorityId == R.id.radio_high) priority = 3;
        }

        // Create new item
        Item newItem = new Item();
        newItem.setName(name);
        newItem.setQuantity(quantity);
        newItem.setCategory(category);
        newItem.setDate(selectedDate);
        newItem.setPriority(priority);
        newItem.setIsChecked(false);

        // Add to database
        long id = dbHelper.addItem(newItem);
        newItem.setId((int) id);

        // Add to list and update RecyclerView
        itemList.add(0, newItem);
        adapter.notifyItemInserted(0);
        recyclerView.scrollToPosition(0);

        // Clear form
        editName.setText("");
        editQuantity.setText("");
        radioCategory.clearCheck();
        radioPriority.clearCheck();
        selectedDate = "";
        btnPickDate.setText("Pick Date");

        Toast.makeText(this, "Item added successfully", Toast.LENGTH_SHORT).show();
    }

    private void showItemDetails(Item item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Item Details")
                .setMessage(
                        "Name: " + item.getName() + "\n" +
                                "Quantity: " + item.getQuantity() + "\n" +
                                "Category: " + item.getCategory() + "\n" +
                                "Date: " + item.getDate() + "\n" +
                                "Priority: " + (item.getPriority() == 3 ? "High" :
                                item.getPriority() == 2 ? "Medium" : "Low") + "\n" +
                                "Status: " + (item.getIsChecked() ? "Purchased" : "Pending")
                )
                .setPositiveButton("OK", null)
                .show();
    }

    private void showEditDialog(Item item, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_edit_item, null);
        builder.setView(dialogView);

        EditText editDialogName = dialogView.findViewById(R.id.edit_dialog_name);
        EditText editDialogQuantity = dialogView.findViewById(R.id.edit_dialog_quantity);
        RadioGroup radioDialogCategory = dialogView.findViewById(R.id.radio_dialog_category);
        RadioGroup radioDialogPriority = dialogView.findViewById(R.id.radio_dialog_priority);
        Button btnDialogDate = dialogView.findViewById(R.id.btn_dialog_date);
        CheckBox checkDialogPurchased = dialogView.findViewById(R.id.check_dialog_purchased);

        // Set current values
        editDialogName.setText(item.getName());
        editDialogQuantity.setText(item.getQuantity());

        // Set category radio - HANYA 3 KATEGORI
        switch (item.getCategory()) {
            case "Food":
                radioDialogCategory.check(R.id.radio_dialog_food);
                break;
            case "Drink":
                radioDialogCategory.check(R.id.radio_dialog_drink);
                break;
            case "Household":
                radioDialogCategory.check(R.id.radio_dialog_household);
                break;
            default:
                radioDialogCategory.check(R.id.radio_dialog_household); // Default ke Household
                break;
        }

        // Set priority radio
        switch (item.getPriority()) {
            case 3:
                radioDialogPriority.check(R.id.radio_dialog_high);
                break;
            case 2:
                radioDialogPriority.check(R.id.radio_dialog_medium);
                break;
            default:
                radioDialogPriority.check(R.id.radio_dialog_low);
                break;
        }

        // Set date
        btnDialogDate.setText(item.getDate());
        selectedDate = item.getDate();

        // Set checkbox
        checkDialogPurchased.setChecked(item.getIsChecked());

        // Date picker
        btnDialogDate.setOnClickListener(v -> showDatePicker());

        builder.setTitle("Edit Item")
                .setPositiveButton("Save", (dialog, which) -> {
                    // Get updated values
                    String updatedName = editDialogName.getText().toString().trim();
                    String updatedQuantity = editDialogQuantity.getText().toString().trim();

                    if (TextUtils.isEmpty(updatedName)) {
                        Toast.makeText(MainActivity.this, "Name cannot be empty", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Get category dari dialog - HANYA 3 KATEGORI
                    int categoryId = radioDialogCategory.getCheckedRadioButtonId();
                    String updatedCategory = "Household"; // Default ke Household
                    if (categoryId != -1) {
                        RadioButton radioButton = dialogView.findViewById(categoryId);
                        updatedCategory = radioButton.getText().toString();
                    }

                    // Get priority
                    int priorityId = radioDialogPriority.getCheckedRadioButtonId();
                    int updatedPriority = 1;
                    if (priorityId != -1) {
                        if (priorityId == R.id.radio_dialog_medium) updatedPriority = 2;
                        else if (priorityId == R.id.radio_dialog_high) updatedPriority = 3;
                    }

                    // Update item
                    item.setName(updatedName);
                    item.setQuantity(updatedQuantity);
                    item.setCategory(updatedCategory);
                    item.setDate(selectedDate);
                    item.setPriority(updatedPriority);
                    item.setIsChecked(checkDialogPurchased.isChecked());

                    // Update in database
                    dbHelper.updateItem(item);

                    // Update in list
                    adapter.notifyItemChanged(position);

                    Toast.makeText(MainActivity.this, "Item updated", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    protected void onDestroy() {
        dbHelper.close();
        super.onDestroy();
    }
}