package com.rini.keuanganharian;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends Activity {
    private final int BLUE = 0xFF2563EB;
    private final int DARK = 0xFF172554;
    private final int MUTED = 0xFF64748B;
    private final int BG = 0xFFF4F7FC;
    private final int GREEN = 0xFF15803D;
    private final int RED = 0xFFDC2626;

    private LinearLayout root, content, historyContainer;
    private TextView balanceValue, incomeValue, expenseValue, greeting;
    private PrefsManager prefs;
    private TransactionDbHelper db;
    private boolean showBalance = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = new PrefsManager(this);
        db = new TransactionDbHelper(this);
        buildScreen();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Preferensi dibaca ulang setiap layar kembali aktif.
        if (prefs != null) {
            showBalance = prefs.isBalanceVisible();
            refreshDashboard();
        }
    }

    private void buildScreen() {
        root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(BG);

        ScrollView scroll = new ScrollView(this);
        content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setPadding(dp(20), dp(22), dp(20), dp(28));
        scroll.addView(content);
        root.addView(scroll, new LinearLayout.LayoutParams(-1, 0, 1));

        LinearLayout header = new LinearLayout(this);
        header.setGravity(Gravity.CENTER_VERTICAL);
        header.setOrientation(LinearLayout.HORIZONTAL);
        greeting = text("Halo, Teman 👋", 20, DARK, true);
        header.addView(greeting, new LinearLayout.LayoutParams(0, -2, 1));
        Button settings = smallButton("Pengaturan");
        settings.setOnClickListener(v -> showSettings());
        header.addView(settings);
        content.addView(header);

        TextView subtitle = text("Catat uang masuk dan keluar setiap hari.", 13, MUTED, false);
        LinearLayout.LayoutParams subLp = new LinearLayout.LayoutParams(-1, -2);
        subLp.topMargin = dp(5);
        content.addView(subtitle, subLp);

        LinearLayout balanceCard = card(BLUE);
        TextView label = text("TOTAL SALDO", 12, 0xFFDCE8FF, true);
        balanceCard.addView(label);
        balanceValue = text("Rp0", 30, 0xFFFFFFFF, true);
        LinearLayout.LayoutParams valueLp = new LinearLayout.LayoutParams(-1, -2);
        valueLp.topMargin = dp(8);
        balanceCard.addView(balanceValue, valueLp);
        TextView hint = text("Ringkasan keuanganmu", 12, 0xFFE5EDFF, false);
        LinearLayout.LayoutParams hintLp = new LinearLayout.LayoutParams(-1, -2);
        hintLp.topMargin = dp(4);
        balanceCard.addView(hint, hintLp);
        LinearLayout.LayoutParams cardLp = new LinearLayout.LayoutParams(-1, -2);
        cardLp.topMargin = dp(22);
        content.addView(balanceCard, cardLp);

        LinearLayout totals = new LinearLayout(this);
        totals.setOrientation(LinearLayout.HORIZONTAL);
        totals.addView(totalCard("Pemasukan", GREEN, true), new LinearLayout.LayoutParams(0, -2, 1));
        View gap = new View(this);
        totals.addView(gap, new LinearLayout.LayoutParams(dp(12), 1));
        totals.addView(totalCard("Pengeluaran", RED, false), new LinearLayout.LayoutParams(0, -2, 1));
        LinearLayout.LayoutParams totalsLp = new LinearLayout.LayoutParams(-1, -2);
        totalsLp.topMargin = dp(14);
        content.addView(totals, totalsLp);

        TextView actionTitle = text("Tambah transaksi", 17, DARK, true);
        LinearLayout.LayoutParams actionTitleLp = new LinearLayout.LayoutParams(-1, -2);
        actionTitleLp.topMargin = dp(25);
        content.addView(actionTitle, actionTitleLp);

        LinearLayout actions = new LinearLayout(this);
        actions.setOrientation(LinearLayout.HORIZONTAL);
        Button addIncome = primaryButton("+ Pemasukan", GREEN);
        addIncome.setOnClickListener(v -> showAddDialog("Pemasukan"));
        Button addExpense = primaryButton("− Pengeluaran", RED);
        addExpense.setOnClickListener(v -> showAddDialog("Pengeluaran"));
        actions.addView(addIncome, new LinearLayout.LayoutParams(0, dp(50), 1));
        View actionGap = new View(this);
        actions.addView(actionGap, new LinearLayout.LayoutParams(dp(10), 1));
        actions.addView(addExpense, new LinearLayout.LayoutParams(0, dp(50), 1));
        LinearLayout.LayoutParams actionsLp = new LinearLayout.LayoutParams(-1, -2);
        actionsLp.topMargin = dp(12);
        content.addView(actions, actionsLp);

        LinearLayout historyHeader = new LinearLayout(this);
        historyHeader.setGravity(Gravity.CENTER_VERTICAL);
        TextView historyTitle = text("Riwayat transaksi", 17, DARK, true);
        historyHeader.addView(historyTitle, new LinearLayout.LayoutParams(0, -2, 1));
        TextView today = text("Terbaru", 12, MUTED, false);
        historyHeader.addView(today);
        LinearLayout.LayoutParams historyLp = new LinearLayout.LayoutParams(-1, -2);
        historyLp.topMargin = dp(27);
        content.addView(historyHeader, historyLp);

        historyContainer = new LinearLayout(this);
        historyContainer.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams listLp = new LinearLayout.LayoutParams(-1, -2);
        listLp.topMargin = dp(10);
        content.addView(historyContainer, listLp);

        setContentView(root);
        refreshDashboard();
    }

    private View totalCard(String title, int accent, boolean income) {
        LinearLayout box = new LinearLayout(this);
        box.setOrientation(LinearLayout.VERTICAL);
        box.setPadding(dp(14), dp(15), dp(14), dp(15));
        box.setBackground(roundRect(0xFFFFFFFF, 16));
        TextView name = text(title, 12, MUTED, false);
        box.addView(name);
        TextView value = text("Rp0", 17, accent, true);
        value.setTag(income ? "income" : "expense");
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, -2);
        lp.topMargin = dp(8);
        box.addView(value, lp);
        if (income) incomeValue = value; else expenseValue = value;
        return box;
    }

    private LinearLayout card(int color) {
        LinearLayout box = new LinearLayout(this);
        box.setOrientation(LinearLayout.VERTICAL);
        box.setPadding(dp(20), dp(20), dp(20), dp(20));
        box.setBackground(roundRect(color, 20));
        return box;
    }

    private android.graphics.drawable.GradientDrawable roundRect(int color, int radius) {
        android.graphics.drawable.GradientDrawable d = new android.graphics.drawable.GradientDrawable();
        d.setColor(color);
        d.setCornerRadius(dp(radius));
        return d;
    }

    private TextView text(String value, int size, int color, boolean bold) {
        TextView t = new TextView(this);
        t.setText(value);
        t.setTextSize(size);
        t.setTextColor(color);
        if (bold) t.setTypeface(null, android.graphics.Typeface.BOLD);
        return t;
    }

    private Button primaryButton(String label, int color) {
        Button b = new Button(this);
        b.setText(label);
        b.setTextColor(0xFFFFFFFF);
        b.setTextSize(13);
        b.setAllCaps(false);
        b.setTypeface(null, android.graphics.Typeface.BOLD);
        b.setBackground(roundRect(color, 14));
        return b;
    }

    private Button smallButton(String label) {
        Button b = new Button(this);
        b.setText(label);
        b.setTextSize(11);
        b.setTextColor(BLUE);
        b.setAllCaps(false);
        b.setPadding(dp(8), 0, dp(8), 0);
        b.setBackground(roundRect(0xFFE7EFFF, 12));
        return b;
    }

    private void showAddDialog(String type) {
        LinearLayout form = new LinearLayout(this);
        form.setOrientation(LinearLayout.VERTICAL);
        form.setPadding(dp(20), dp(8), dp(20), 0);

        EditText amount = new EditText(this);
        amount.setHint("Nominal");
        amount.setInputType(android.text.InputType.TYPE_CLASS_NUMBER | android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL);
        form.addView(amount);

        EditText note = new EditText(this);
        note.setHint("Keterangan");
        form.addView(note);

        new AlertDialog.Builder(this)
                .setTitle("Tambah " + type)
                .setView(form)
                .setNegativeButton("Batal", null)
                .setPositiveButton("Simpan", (dialog, which) -> {
                    String raw = amount.getText().toString().trim().replace(",", ".");
                    try {
                        double value = Double.parseDouble(raw);
                        if (value <= 0) throw new NumberFormatException();
                        db.insert(type, value, note.getText().toString().trim());
                        refreshDashboard();
                        Toast.makeText(this, type + " berhasil disimpan", Toast.LENGTH_SHORT).show();
                    } catch (NumberFormatException e) {
                        Toast.makeText(this, "Masukkan nominal yang valid", Toast.LENGTH_SHORT).show();
                    }
                }).show();
    }

    private void showSettings() {
        LinearLayout panel = new LinearLayout(this);
        panel.setOrientation(LinearLayout.VERTICAL);
        panel.setPadding(dp(20), dp(8), dp(20), 0);

        EditText name = new EditText(this);
        name.setHint("Nama panggilan");
        name.setText(prefs.getUserName());
        panel.addView(name);

        Switch balanceSwitch = new Switch(this);
        balanceSwitch.setText("Tampilkan total saldo");
        balanceSwitch.setTextSize(14);
        balanceSwitch.setChecked(prefs.isBalanceVisible());
        LinearLayout.LayoutParams swLp = new LinearLayout.LayoutParams(-1, -2);
        swLp.topMargin = dp(12);
        panel.addView(balanceSwitch, swLp);

        new AlertDialog.Builder(this)
                .setTitle("Pengaturan aplikasi")
                .setView(panel)
                .setNegativeButton("Batal", null)
                .setPositiveButton("Simpan", (d, w) -> {
                    String userName = name.getText().toString().trim();
                    prefs.setUserName(userName.isEmpty() ? "Teman" : userName);
                    prefs.setBalanceVisible(balanceSwitch.isChecked());
                    showBalance = balanceSwitch.isChecked();
                    refreshDashboard();
                    Toast.makeText(this, "Pengaturan tersimpan", Toast.LENGTH_SHORT).show();
                }).show();
    }

    private void refreshDashboard() {
        if (db == null || balanceValue == null) return;
        double income = db.getTotal("Pemasukan");
        double expense = db.getTotal("Pengeluaran");
        double balance = income - expense;

        greeting.setText("Halo, " + prefs.getUserName() + " 👋");
        balanceValue.setText(showBalance ? rupiah(balance) : "••••••••");
        incomeValue.setText(rupiah(income));
        expenseValue.setText(rupiah(expense));

        historyContainer.removeAllViews();
        List<TransactionDbHelper.TransactionItem> items = db.getAll();
        if (items.isEmpty()) {
            TextView empty = text("Belum ada transaksi. Yuk, catat pemasukan atau pengeluaran pertamamu.", 13, MUTED, false);
            empty.setPadding(dp(16), dp(20), dp(16), dp(20));
            empty.setGravity(Gravity.CENTER);
            empty.setBackground(roundRect(0xFFFFFFFF, 16));
            historyContainer.addView(empty);
            return;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy • HH:mm", new Locale("id", "ID"));
        int limit = Math.min(items.size(), 30);
        for (int i = 0; i < limit; i++) {
            TransactionDbHelper.TransactionItem item = items.get(i);
            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setGravity(Gravity.CENTER_VERTICAL);
            row.setPadding(dp(14), dp(13), dp(14), dp(13));
            row.setBackground(roundRect(0xFFFFFFFF, 14));

            TextView symbol = text(item.type.equals("Pemasukan") ? "+" : "−", 22,
                    item.type.equals("Pemasukan") ? GREEN : RED, true);
            symbol.setGravity(Gravity.CENTER);
            row.addView(symbol, new LinearLayout.LayoutParams(dp(34), dp(34)));

            LinearLayout details = new LinearLayout(this);
            details.setOrientation(LinearLayout.VERTICAL);
            TextView title = text(item.note.isEmpty() ? item.type : item.note, 14, DARK, true);
            TextView date = text(sdf.format(new Date(item.createdAt)), 11, MUTED, false);
            details.addView(title);
            LinearLayout.LayoutParams dateLp = new LinearLayout.LayoutParams(-1, -2);
            dateLp.topMargin = dp(4);
            details.addView(date, dateLp);
            LinearLayout.LayoutParams detailLp = new LinearLayout.LayoutParams(0, -2, 1);
            detailLp.leftMargin = dp(10);
            row.addView(details, detailLp);

            TextView amount = text((item.type.equals("Pemasukan") ? "+" : "−") + rupiah(item.amount),
                    13, item.type.equals("Pemasukan") ? GREEN : RED, true);
            row.addView(amount);

            LinearLayout.LayoutParams rowLp = new LinearLayout.LayoutParams(-1, -2);
            rowLp.bottomMargin = dp(8);
            historyContainer.addView(row, rowLp);
        }
    }

    private String rupiah(double value) {
        NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        format.setMaximumFractionDigits(0);
        format.setMinimumFractionDigits(0);
        return format.format(value);
    }

    private int dp(int value) {
        return (int) (value * getResources().getDisplayMetrics().density + 0.5f);
    }
}
