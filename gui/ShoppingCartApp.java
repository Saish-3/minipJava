// ============================================================
// gui/ShoppingCartApp.java
// Package  : gui  (Graphical User Interface)
//
// Main Swing GUI for the Online Shopping Cart Management System.
// Built using standard Java Swing — no external libraries.
//
// Features:
//   - Products Tab: View, filter by category, search by ID/keyword
//   - Cart Tab: Add, update qty, remove, clear, coupon/discount
//   - Checkout: Subtotal → Discount → Grand Total receipt
//   - Dark-themed, modern Swing UI
// ============================================================

package gui;

import datastructure.ProductDatabase;
import oops.Cart;
import oops.CartItem;
import oops.Customer;
import oops.Order;
import oops.Product;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class ShoppingCartApp extends JFrame {

    // ============================================================
    //  Business Layer — Data & Logic
    // ============================================================
    private ProductDatabase db;       // datastructure package
    private Cart            cart;     // oops package
    private Customer        customer; // oops package

    // ============================================================
    //  Design Tokens — Colours & Fonts
    // ============================================================

    // --- Colour palette ---
    private static final Color BG_DARK      = new Color(18,  18,  35);
    private static final Color BG_PANEL     = new Color(28,  28,  50);
    private static final Color BG_CARD      = new Color(38,  38,  65);
    private static final Color ACCENT_BLUE  = new Color(64,  122, 255);
    private static final Color ACCENT_GREEN = new Color(46,  213, 115);
    private static final Color ACCENT_RED   = new Color(255,  72,  72);
    private static final Color ACCENT_GOLD  = new Color(255, 196,  64);
    private static final Color TEXT_WHITE   = new Color(240, 240, 255);
    private static final Color TEXT_GREY    = new Color(160, 160, 200);
    private static final Color TABLE_ALT    = new Color(33,  33,  60);

    // --- Fonts ---
    private static final Font FONT_TITLE   = new Font("Segoe UI", Font.BOLD,  22);
    private static final Font FONT_HEADING = new Font("Segoe UI", Font.BOLD,  14);
    private static final Font FONT_BODY    = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font FONT_SMALL   = new Font("Segoe UI", Font.PLAIN, 12);
    private static final Font FONT_MONO    = new Font("Consolas",  Font.PLAIN, 12);

    // ============================================================
    //  GUI Component Declarations
    // ============================================================

    // --- Product table (Products tab) ---
    private JTable            productTable;
    private DefaultTableModel productTableModel;

    // --- Cart table (Cart tab) ---
    private JTable            cartTable;
    private DefaultTableModel cartTableModel;

    // --- Search & input fields ---
    private JTextField        searchField;
    private JTextField        addProductIdField;
    private JTextField        addQuantityField;
    private JTextField        updateProductIdField;
    private JTextField        updateQuantityField;
    private JTextField        removeProductIdField;

    // --- Category filter dropdown (Products tab) ---
    private JComboBox<String> categoryCombo;

    // --- Coupon / discount (Cart tab) ---
    private JTextField        couponField;
    private JLabel            couponStatusLabel;

    // --- Header badges ---
    private JLabel            statusLabel;
    private JLabel            cartCountLabel;
    private JLabel            cartTotalLabel;
    private JLabel            customerInfoLabel;

    // --- Tab pane ---
    private JTabbedPane       tabbedPane;

    // ============================================================
    //  Constructor
    // ============================================================
    public ShoppingCartApp() {
        db       = new ProductDatabase();
        cart     = new Cart();
        customer = new Customer(1, "Saish Patil", "saish@example.com", "9876543210");

        initUI();
        setVisible(true);
    }

    // ============================================================
    //  UI Initialisation
    // ============================================================
    private void initUI() {
        setTitle("Online Shopping Cart Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 720);
        setMinimumSize(new Dimension(900, 620));
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout(0, 0));
        root.setBackground(BG_DARK);
        setContentPane(root);

        root.add(buildHeaderPanel(), BorderLayout.NORTH);

        tabbedPane = new JTabbedPane();
        tabbedPane.setBackground(BG_PANEL);
        tabbedPane.setForeground(TEXT_WHITE);
        tabbedPane.setFont(FONT_HEADING);
        styleTabPane(tabbedPane);
        tabbedPane.addTab(" 🛍  Products ", buildProductsTab());
        tabbedPane.addTab(" 🛒  My Cart  ", buildCartTab());
        root.add(tabbedPane, BorderLayout.CENTER);

        root.add(buildStatusBar(), BorderLayout.SOUTH);

        refreshProductTable(db.getAllProductsSorted());
    }

    // ============================================================
    //  Header Panel
    // ============================================================
    private JPanel buildHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(BG_PANEL);
        header.setBorder(new EmptyBorder(14, 20, 14, 20));

        JLabel title = new JLabel("  🛒  Online Shopping Cart");
        title.setFont(FONT_TITLE);
        title.setForeground(ACCENT_BLUE);

        customerInfoLabel = new JLabel("👤 " + customer.getName() + "  |  " + customer.getEmail());
        customerInfoLabel.setFont(FONT_BODY);
        customerInfoLabel.setForeground(TEXT_GREY);
        customerInfoLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel cartBadge = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        cartBadge.setOpaque(false);
        cartCountLabel = new JLabel("Items: 0");
        cartCountLabel.setFont(FONT_BODY);
        cartCountLabel.setForeground(ACCENT_GOLD);
        cartTotalLabel = new JLabel("Total: Rs.0.00");
        cartTotalLabel.setFont(FONT_HEADING);
        cartTotalLabel.setForeground(ACCENT_GREEN);
        cartBadge.add(cartCountLabel);
        cartBadge.add(new JSeparator(SwingConstants.VERTICAL));
        cartBadge.add(cartTotalLabel);

        header.add(title, BorderLayout.WEST);
        header.add(customerInfoLabel, BorderLayout.CENTER);
        header.add(cartBadge, BorderLayout.EAST);

        JPanel sep = new JPanel();
        sep.setBackground(ACCENT_BLUE);
        sep.setPreferredSize(new Dimension(0, 2));
        header.add(sep, BorderLayout.SOUTH);

        return header;
    }

    // ============================================================
    //  Products Tab
    // ============================================================
    private JPanel buildProductsTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(BG_DARK);
        panel.setBorder(new EmptyBorder(12, 12, 12, 12));

        panel.add(buildSearchPanel(), BorderLayout.NORTH);

        // ---- Product table ----
        productTableModel = new DefaultTableModel(
            new String[]{"ID", "Product Name", "Category", "Price (Rs.)", "Stock"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        productTable = new JTable(productTableModel);
        styleTable(productTable);
        productTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        productTable.getColumnModel().getColumn(1).setPreferredWidth(220);
        productTable.getColumnModel().getColumn(2).setPreferredWidth(120);
        productTable.getColumnModel().getColumn(3).setPreferredWidth(100);
        productTable.getColumnModel().getColumn(4).setPreferredWidth(60);
        panel.add(styledScrollPane(productTable), BorderLayout.CENTER);

        panel.add(buildAddToCartPanel(), BorderLayout.SOUTH);
        return panel;
    }

    // ---- Search + Category Filter Bar (Products Tab) ----
    private JPanel buildSearchPanel() {
        JPanel wrapper = new JPanel(new BorderLayout(0, 4));
        wrapper.setOpaque(false);

        // Row 1: Keyword search
        JPanel searchRow = createCard();
        searchRow.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 8));

        JLabel lbl = new JLabel("🔍 Search:");
        lbl.setForeground(TEXT_WHITE);
        lbl.setFont(FONT_BODY);

        searchField = createTextField(22);
        searchField.setToolTipText("Search by product ID, name, or keyword");

        JButton searchBtn  = createButton("Search",     ACCENT_BLUE);
        JButton clearBtn   = createButton("Show All",   BG_CARD);
        JButton refreshBtn = createButton("⟳ Refresh", new Color(80, 80, 120));

        searchBtn.addActionListener(e -> doSearch());
        searchField.addActionListener(e -> doSearch());

        clearBtn.addActionListener(e -> {
            searchField.setText("");
            categoryCombo.setSelectedIndex(0);
            refreshProductTable(db.getAllProductsSorted());
            setStatus("Showing all " + db.getProductCount() + " products.");
        });
        refreshBtn.addActionListener(e -> {
            refreshProductTable(db.getAllProductsSorted());
            setStatus("Product list refreshed.");
        });

        searchRow.add(lbl);
        searchRow.add(searchField);
        searchRow.add(searchBtn);
        searchRow.add(clearBtn);
        searchRow.add(refreshBtn);

        // Row 2: Category filter
        JPanel filterRow = createCard();
        filterRow.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 6));

        JLabel catLbl = new JLabel("📂 Filter by Category:");
        catLbl.setForeground(ACCENT_GOLD);
        catLbl.setFont(FONT_BODY);

        // Build category list dynamically from TreeMap
        java.util.Set<String> cats = new java.util.TreeSet<>();
        for (Product p : db.getAllProductsSorted()) cats.add(p.getCategory());
        String[] catArray = new String[cats.size() + 1];
        catArray[0] = "All Categories";
        int ci = 1;
        for (String c : cats) catArray[ci++] = c;

        categoryCombo = new JComboBox<>(catArray);
        styleComboBox(categoryCombo);

        JLabel countBadge = new JLabel(db.getProductCount() + " products");
        countBadge.setFont(FONT_SMALL);
        countBadge.setForeground(TEXT_GREY);

        categoryCombo.addActionListener(e -> {
            applyCategoryFilter();
            String sel = (String) categoryCombo.getSelectedItem();
            if ("All Categories".equals(sel)) {
                countBadge.setText(db.getProductCount() + " products");
            } else {
                long cnt = db.getAllProductsSorted().stream()
                        .filter(p -> p.getCategory().equals(sel)).count();
                countBadge.setText(cnt + " product(s) in category");
            }
        });

        filterRow.add(catLbl);
        filterRow.add(categoryCombo);
        filterRow.add(countBadge);

        wrapper.add(searchRow,  BorderLayout.NORTH);
        wrapper.add(filterRow,  BorderLayout.SOUTH);
        return wrapper;
    }

    // ---- Apply selected category filter ----
    private void applyCategoryFilter() {
        String sel = (String) categoryCombo.getSelectedItem();
        if ("All Categories".equals(sel)) {
            refreshProductTable(db.getAllProductsSorted());
            setStatus("Showing all " + db.getProductCount() + " products.");
        } else {
            java.util.List<Product> filtered = new java.util.ArrayList<>();
            for (Product p : db.getAllProductsSorted()) {
                if (p.getCategory().equals(sel)) filtered.add(p);
            }
            refreshProductTable(filtered);
            setStatus("Category: " + sel + " — " + filtered.size() + " product(s) found.");
        }
    }

    // ---- Add to Cart Panel (bottom of Products Tab) ----
    private JPanel buildAddToCartPanel() {
        JPanel card = createCard();
        card.setLayout(new FlowLayout(FlowLayout.LEFT, 12, 10));

        JLabel h = new JLabel("Add to Cart:");
        h.setFont(FONT_HEADING);
        h.setForeground(ACCENT_GREEN);

        addProductIdField = createTextField(8);
        addProductIdField.setToolTipText("Enter Product ID");
        addQuantityField  = createTextField(6);
        addQuantityField.setToolTipText("Enter quantity");

        JButton addBtn = createButton("➕  Add to Cart", ACCENT_GREEN);
        addBtn.addActionListener(e -> doAddToCart());
        addQuantityField.addActionListener(e -> doAddToCart());

        // Double-click product row → auto-fill ID field
        productTable.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = productTable.getSelectedRow();
                    if (row >= 0) {
                        int pid = (int) productTableModel.getValueAt(row, 0);
                        addProductIdField.setText(String.valueOf(pid));
                        addQuantityField.requestFocus();
                        addQuantityField.selectAll();
                    }
                }
            }
        });

        JLabel hint = new JLabel("  (Tip: double-click a row to auto-fill Product ID)");
        hint.setFont(FONT_SMALL);
        hint.setForeground(TEXT_GREY);

        card.add(h);
        card.add(makeLabel("Product ID:")); card.add(addProductIdField);
        card.add(makeLabel("Quantity:"));   card.add(addQuantityField);
        card.add(addBtn);
        card.add(hint);
        return card;
    }

    // ============================================================
    //  Cart Tab
    // ============================================================
    private JPanel buildCartTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(BG_DARK);
        panel.setBorder(new EmptyBorder(12, 12, 12, 12));

        // Cart table
        cartTableModel = new DefaultTableModel(
            new String[]{"Product ID", "Product Name", "Unit Price (Rs.)", "Quantity", "Subtotal (Rs.)"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        cartTable = new JTable(cartTableModel);
        styleTable(cartTable);
        cartTable.getColumnModel().getColumn(0).setPreferredWidth(80);
        cartTable.getColumnModel().getColumn(1).setPreferredWidth(220);
        cartTable.getColumnModel().getColumn(2).setPreferredWidth(130);
        cartTable.getColumnModel().getColumn(3).setPreferredWidth(80);
        cartTable.getColumnModel().getColumn(4).setPreferredWidth(120);
        panel.add(styledScrollPane(cartTable), BorderLayout.CENTER);

        panel.add(buildCartControlsPanel(), BorderLayout.SOUTH);
        return panel;
    }

    // ---- Cart Controls: Update / Coupon / Remove / Checkout ----
    private JPanel buildCartControlsPanel() {
        JPanel wrapper = new JPanel(new BorderLayout(0, 6));
        wrapper.setBackground(BG_DARK);

        // Row 1: Update quantity
        JPanel updatePanel = createCard();
        updatePanel.setLayout(new FlowLayout(FlowLayout.LEFT, 12, 8));
        updatePanel.add(makeBold("Update Qty:"));
        updatePanel.add(makeLabel("Product ID:"));
        updateProductIdField = createTextField(7);
        updatePanel.add(updateProductIdField);
        updatePanel.add(makeLabel("New Qty:"));
        updateQuantityField = createTextField(5);
        updatePanel.add(updateQuantityField);
        JButton updateBtn = createButton("✏  Update", ACCENT_BLUE);
        updateBtn.addActionListener(e -> doUpdateQuantity());
        updatePanel.add(updateBtn);

        // Row 2: Coupon / Discount
        JPanel couponPanel = createCard();
        couponPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 12, 8));
        couponPanel.add(makeBold("🏷  Coupon:"));
        couponField = createTextField(12);
        couponField.setToolTipText("Enter coupon code, e.g. SAVE10");
        couponPanel.add(couponField);

        JButton applyBtn = createButton("Apply", new Color(130, 80, 200));
        applyBtn.addActionListener(e -> applyCoupon());
        couponField.addActionListener(e -> applyCoupon());
        couponPanel.add(applyBtn);

        JButton removeCouponBtn = createButton("✕ Remove", new Color(100, 40, 40));
        removeCouponBtn.addActionListener(e -> {
            couponField.setText("");
            couponStatusLabel.setText("No coupon applied.");
            couponStatusLabel.setForeground(TEXT_GREY);
            setStatus("Coupon removed.");
        });
        couponPanel.add(removeCouponBtn);

        couponStatusLabel = new JLabel("No coupon applied.");
        couponStatusLabel.setFont(FONT_SMALL);
        couponStatusLabel.setForeground(TEXT_GREY);
        couponPanel.add(couponStatusLabel);

        JLabel couponHint = new JLabel("  Try: SAVE10  |  FLAT15  |  WELCOME20");
        couponHint.setFont(FONT_SMALL);
        couponHint.setForeground(new Color(130, 80, 200));
        couponPanel.add(couponHint);

        // Row 3: Remove & main action buttons
        JPanel actionPanel = createCard();
        actionPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 12, 8));
        actionPanel.add(makeBold("Remove Item:"));
        actionPanel.add(makeLabel("Product ID:"));
        removeProductIdField = createTextField(7);
        actionPanel.add(removeProductIdField);
        JButton removeBtn = createButton("🗑  Remove", ACCENT_RED);
        removeBtn.addActionListener(e -> doRemoveItem());
        actionPanel.add(removeBtn);
        actionPanel.add(new JLabel("   "));

        JButton clearBtn    = createButton("🧹  Clear Cart", new Color(120, 80, 40));
        JButton checkoutBtn = createButton("💳  Checkout",  ACCENT_GOLD);
        clearBtn.setForeground(TEXT_WHITE);
        checkoutBtn.setForeground(new Color(20, 20, 20));
        clearBtn.addActionListener(e -> doClearCart());
        checkoutBtn.addActionListener(e -> doCheckout());
        actionPanel.add(clearBtn);
        actionPanel.add(checkoutBtn);

        // Double-click cart row → auto-fill update/remove ID fields
        cartTable.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = cartTable.getSelectedRow();
                    if (row >= 0) {
                        int pid = (int) cartTableModel.getValueAt(row, 0);
                        updateProductIdField.setText(String.valueOf(pid));
                        removeProductIdField.setText(String.valueOf(pid));
                        updateQuantityField.requestFocus();
                    }
                }
            }
        });

        JPanel topRows = new JPanel(new BorderLayout(0, 6));
        topRows.setBackground(BG_DARK);
        topRows.add(updatePanel, BorderLayout.NORTH);
        topRows.add(couponPanel, BorderLayout.SOUTH);

        wrapper.add(topRows,     BorderLayout.NORTH);
        wrapper.add(actionPanel, BorderLayout.SOUTH);
        return wrapper;
    }

    // ============================================================
    //  Status Bar
    // ============================================================
    private JPanel buildStatusBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(new Color(22, 22, 40));
        bar.setBorder(new EmptyBorder(5, 14, 5, 14));

        statusLabel = new JLabel("Ready. Welcome, " + customer.getName() + "!");
        statusLabel.setFont(FONT_SMALL);
        statusLabel.setForeground(TEXT_GREY);

        JLabel copy = new JLabel("Online Shopping Cart Management System  |  Java Swing Mini Project");
        copy.setFont(FONT_SMALL);
        copy.setForeground(new Color(100, 100, 140));

        bar.add(statusLabel, BorderLayout.WEST);
        bar.add(copy, BorderLayout.EAST);
        return bar;
    }

    // ============================================================
    //  Valid Coupons  (extend by adding more entries here)
    // ============================================================
    private static final java.util.Map<String, Integer> VALID_COUPONS = new java.util.HashMap<>();
    static {
        VALID_COUPONS.put("SAVE10",    10);  // 10% discount
        VALID_COUPONS.put("FLAT15",    15);  // 15% discount
        VALID_COUPONS.put("WELCOME20", 20);  // 20% discount
    }

    // ============================================================
    //  Business Logic — Action Handlers
    // ============================================================

    // ---- Search: keyword + category filter combined ----
    private void doSearch() {
        String keyword     = searchField.getText().trim();
        String selectedCat = (String) categoryCombo.getSelectedItem();
        boolean hasCat     = !"All Categories".equals(selectedCat);

        if (keyword.isEmpty() && !hasCat) {
            setStatus("Please enter a search term or select a category.");
            return;
        }
        if (keyword.isEmpty()) { applyCategoryFilter(); return; }

        try {
            int pid = Integer.parseInt(keyword);
            Product p = db.getProductById(pid);
            if (p != null && (!hasCat || p.getCategory().equals(selectedCat))) {
                refreshProductTable(Collections.singletonList(p));
                setStatus("Found: " + p.getName());
            } else {
                refreshProductTable(Collections.emptyList());
                setStatus("No product found with ID: " + pid
                        + (hasCat ? " in '" + selectedCat + "'" : ""));
            }
        } catch (NumberFormatException ex) {
            java.util.List<Product> results = db.searchByName(keyword);
            if (hasCat) results.removeIf(p -> !p.getCategory().equals(selectedCat));
            refreshProductTable(results);
            setStatus(results.isEmpty()
                ? "No match for \"" + keyword + "\"" + (hasCat ? " in '" + selectedCat + "'" : "")
                : "Found " + results.size() + " product(s).");
        }
    }

    // ---- Add product to cart ----
    private void doAddToCart() {
        String pidText = addProductIdField.getText().trim();
        String qtyText = addQuantityField.getText().trim();

        if (pidText.isEmpty() || qtyText.isEmpty()) {
            showError("Please enter both Product ID and Quantity.");
            return;
        }
        int pid, qty;
        try { pid = Integer.parseInt(pidText); }
        catch (NumberFormatException e) { showError("Product ID must be a valid number."); return; }
        try { qty = Integer.parseInt(qtyText); }
        catch (NumberFormatException e) { showError("Quantity must be a valid number."); return; }

        Product product = db.getProductById(pid);
        if (product == null) { showError("Product ID " + pid + " does not exist."); return; }
        if (qty <= 0)        { showError("Quantity must be greater than zero."); return; }

        String result = cart.addItem(product, qty);
        if (result.contains("success") || result.contains("updated")) {
            showInfo(result);
            addProductIdField.setText("");
            addQuantityField.setText("");
            refreshCartTable();
            refreshProductTable(db.getAllProductsSorted());
            updateCartBadge();
            setStatus(result + "  |  Cart has " + cart.getItemCount() + " item(s).");
            tabbedPane.setSelectedIndex(1); // switch to cart tab
        } else {
            showError(result);
        }
    }

    // ---- Update cart item quantity ----
    private void doUpdateQuantity() {
        String pidText = updateProductIdField.getText().trim();
        String qtyText = updateQuantityField.getText().trim();
        if (pidText.isEmpty() || qtyText.isEmpty()) {
            showError("Please enter Product ID and new Quantity.");
            return;
        }
        int pid, qty;
        try { pid = Integer.parseInt(pidText); }
        catch (NumberFormatException e) { showError("Product ID must be a valid number."); return; }
        try { qty = Integer.parseInt(qtyText); }
        catch (NumberFormatException e) { showError("Quantity must be a valid number."); return; }

        String result = cart.updateQuantity(pid, qty);
        if (result.contains("updated")) {
            showInfo(result);
            updateProductIdField.setText("");
            updateQuantityField.setText("");
        } else {
            showError(result);
        }
        refreshCartTable();
        refreshProductTable(db.getAllProductsSorted());
        updateCartBadge();
        setStatus(result);
    }

    // ---- Remove item from cart ----
    private void doRemoveItem() {
        String pidText = removeProductIdField.getText().trim();
        if (pidText.isEmpty()) { showError("Please enter a Product ID to remove."); return; }
        int pid;
        try { pid = Integer.parseInt(pidText); }
        catch (NumberFormatException e) { showError("Product ID must be a valid number."); return; }

        int confirm = JOptionPane.showConfirmDialog(this,
            "Remove product ID " + pid + " from cart?",
            "Confirm Remove", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) return;

        String result = cart.removeItem(pid);
        if (result.contains("removed")) {
            showInfo(result);
            removeProductIdField.setText("");
        } else {
            showError(result);
        }
        refreshCartTable();
        refreshProductTable(db.getAllProductsSorted());
        updateCartBadge();
        setStatus(result);
    }

    // ---- Clear entire cart ----
    private void doClearCart() {
        if (cart.isEmpty()) { showError("Cart is already empty."); return; }
        int confirm = JOptionPane.showConfirmDialog(this,
            "Clear all items from the cart?",
            "Clear Cart", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) return;
        cart.clearCart();
        refreshCartTable();
        refreshProductTable(db.getAllProductsSorted());
        updateCartBadge();
        setStatus("Cart cleared.");
    }

    // ---- Apply coupon code ----
    private void applyCoupon() {
        String code = couponField.getText().trim().toUpperCase();
        if (code.isEmpty()) {
            couponStatusLabel.setText("Please enter a coupon code.");
            couponStatusLabel.setForeground(ACCENT_RED);
            return;
        }
        if (VALID_COUPONS.containsKey(code)) {
            int pct = VALID_COUPONS.get(code);
            couponStatusLabel.setText("✅  Coupon " + code + " applied! " + pct + "% off at checkout.");
            couponStatusLabel.setForeground(ACCENT_GREEN);
            couponField.setText(code);
            setStatus("Coupon " + code + " valid — " + pct + "% discount will be applied.");
        } else {
            couponStatusLabel.setText("❌  Invalid coupon: " + code);
            couponStatusLabel.setForeground(ACCENT_RED);
            setStatus("Invalid coupon code.");
        }
    }

    // ---- Checkout ----
    private void doCheckout() {
        if (cart.isEmpty()) { showError("Your cart is empty! Add products before checking out."); return; }

        // Resolve coupon
        String couponCode = couponField.getText().trim().toUpperCase();
        int discountPct = 0;
        if (!couponCode.isEmpty()) {
            if (VALID_COUPONS.containsKey(couponCode)) {
                discountPct = VALID_COUPONS.get(couponCode);
            } else {
                int fix = JOptionPane.showConfirmDialog(this,
                    "Coupon '" + couponCode + "' is invalid.\nProceed without any discount?",
                    "Invalid Coupon", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (fix != JOptionPane.YES_OPTION) return;
                couponCode = "";
            }
        }

        // Billing preview values
        double subtotal    = cart.getTotal();
        double discountAmt = subtotal * discountPct / 100.0;
        double grandTotal  = subtotal - discountAmt;

        String discountLine = (discountPct > 0)
            ? String.format("\n  Coupon     : %s (%d%% off)\n  Discount   : - Rs.%.2f",
                            couponCode, discountPct, discountAmt)
            : "\n  Coupon     : None";

        int confirm = JOptionPane.showConfirmDialog(this,
            String.format("Proceed to checkout?\n\n  Items      : %d product(s)\n  Total Qty  : %d\n  Subtotal   : Rs.%.2f%s\n  GRAND TOTAL: Rs.%.2f",
                cart.getItemCount(), cart.getTotalQuantity(), subtotal, discountLine, grandTotal),
            "Confirm Checkout", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) return;

        // Create Order (oops package)
        Order order = new Order(customer, cart.getItems(),
                                subtotal, cart.getTotalQuantity(),
                                couponCode, discountPct);

        showOrderReceipt(order);

        // Finalize
        cart.checkoutClear();
        couponField.setText("");
        couponStatusLabel.setText("No coupon applied.");
        couponStatusLabel.setForeground(TEXT_GREY);
        refreshCartTable();
        updateCartBadge();
        refreshProductTable(db.getAllProductsSorted());
        setStatus(String.format("Order #%d placed! Grand Total: Rs.%.2f  Thank you for shopping.",
                order.getOrderId(), order.getGrandTotal()));
    }

    // ============================================================
    //  Table Refresh Helpers
    // ============================================================

    private void refreshProductTable(java.util.Collection<Product> products) {
        productTableModel.setRowCount(0);
        for (Product p : products) {
            productTableModel.addRow(new Object[]{
                p.getProductId(), p.getName(), p.getCategory(),
                String.format("%.2f", p.getPrice()), p.getQuantity()
            });
        }
    }

    private void refreshCartTable() {
        cartTableModel.setRowCount(0);
        for (CartItem item : cart.getItems()) {
            cartTableModel.addRow(new Object[]{
                item.getProduct().getProductId(),
                item.getProduct().getName(),
                String.format("%.2f", item.getProduct().getPrice()),
                item.getQuantity(),
                String.format("%.2f", item.getSubtotal())
            });
        }
    }

    private void updateCartBadge() {
        cartCountLabel.setText("Items: " + cart.getItemCount());
        cartTotalLabel.setText(String.format("Total: Rs.%.2f", cart.getTotal()));
    }

    // ============================================================
    //  Order Receipt Dialog
    // ============================================================
    private void showOrderReceipt(Order order) {
        JDialog dialog = new JDialog(this, "Order Receipt - #" + order.getOrderId(), true);
        dialog.setSize(580, 500);
        dialog.setLocationRelativeTo(this);
        dialog.getContentPane().setBackground(BG_DARK);
        dialog.setLayout(new BorderLayout(10, 10));

        JTextArea receipt = new JTextArea(order.getOrderSummary());
        receipt.setFont(FONT_MONO);
        receipt.setBackground(new Color(10, 12, 25));
        receipt.setForeground(ACCENT_GREEN);
        receipt.setEditable(false);
        receipt.setBorder(new EmptyBorder(16, 16, 16, 16));

        JScrollPane scroll = new JScrollPane(receipt);
        scroll.setBorder(BorderFactory.createLineBorder(ACCENT_BLUE, 1));
        scroll.getViewport().setBackground(new Color(10, 12, 25));

        JButton closeBtn = createButton("✅  Done", ACCENT_GREEN);
        closeBtn.setForeground(new Color(10, 10, 10));
        closeBtn.addActionListener(e -> dialog.dispose());

        JPanel btnPanel = new JPanel();
        btnPanel.setBackground(BG_DARK);
        btnPanel.setBorder(new EmptyBorder(0, 0, 12, 0));
        btnPanel.add(closeBtn);

        JLabel heading = new JLabel("  🎉  Order Confirmed!", SwingConstants.CENTER);
        heading.setFont(new Font("Segoe UI", Font.BOLD, 18));
        heading.setForeground(ACCENT_GREEN);
        heading.setBorder(new EmptyBorder(12, 0, 8, 0));
        heading.setBackground(BG_DARK);
        heading.setOpaque(true);

        dialog.add(heading,  BorderLayout.NORTH);
        dialog.add(scroll,   BorderLayout.CENTER);
        dialog.add(btnPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    // ============================================================
    //  Styling Utilities
    // ============================================================

    private void styleTable(JTable table) {
        table.setBackground(BG_PANEL);
        table.setForeground(TEXT_WHITE);
        table.setFont(FONT_BODY);
        table.setRowHeight(28);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(new Color(64, 100, 200, 180));
        table.setSelectionForeground(Color.WHITE);
        table.getTableHeader().setBackground(BG_CARD);
        table.getTableHeader().setForeground(ACCENT_GOLD);
        table.getTableHeader().setFont(FONT_HEADING);
        table.getTableHeader().setPreferredSize(new Dimension(0, 36));
        table.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, ACCENT_BLUE));

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable t, Object val, boolean selected, boolean focused, int row, int col) {
                super.getTableCellRendererComponent(t, val, selected, focused, row, col);
                setBackground(selected ? new Color(64, 100, 200, 180) : (row % 2 == 0 ? BG_PANEL : TABLE_ALT));
                setForeground(selected ? Color.WHITE : TEXT_WHITE);
                setBorder(new EmptyBorder(0, 10, 0, 10));
                setFont(FONT_BODY);
                return this;
            }
        });
    }

    private JScrollPane styledScrollPane(JTable table) {
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(60, 60, 100), 1));
        scroll.getViewport().setBackground(BG_PANEL);
        scroll.setBackground(BG_DARK);
        return scroll;
    }

    private JPanel createCard() {
        JPanel p = new JPanel();
        p.setBackground(BG_CARD);
        p.setBorder(new CompoundBorder(
            BorderFactory.createLineBorder(new Color(60, 60, 110), 1),
            new EmptyBorder(4, 8, 4, 8)));
        return p;
    }

    private JTextField createTextField(int cols) {
        JTextField tf = new JTextField(cols);
        tf.setBackground(new Color(25, 25, 48));
        tf.setForeground(TEXT_WHITE);
        tf.setCaretColor(TEXT_WHITE);
        tf.setFont(FONT_BODY);
        tf.setBorder(new CompoundBorder(
            BorderFactory.createLineBorder(new Color(80, 80, 150)),
            new EmptyBorder(4, 8, 4, 8)));
        return tf;
    }

    private void styleComboBox(JComboBox<String> combo) {
        combo.setBackground(new Color(25, 25, 48));
        combo.setForeground(TEXT_WHITE);
        combo.setFont(FONT_BODY);
        combo.setFocusable(false);
        combo.setBorder(BorderFactory.createLineBorder(new Color(80, 80, 150)));
        combo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(
                    JList<?> list, Object val, int idx, boolean sel, boolean focus) {
                super.getListCellRendererComponent(list, val, idx, sel, focus);
                setBackground(sel ? ACCENT_BLUE : new Color(25, 25, 48));
                setForeground(sel ? Color.WHITE : TEXT_WHITE);
                setFont(FONT_BODY);
                setBorder(new EmptyBorder(4, 10, 4, 10));
                return this;
            }
        });
    }

    private JButton createButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setBackground(bg);
        btn.setForeground(TEXT_WHITE);
        btn.setFont(FONT_BODY);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(7, 16, 7, 16));
        btn.setOpaque(true);
        Color hoverBg = bg.brighter();
        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { btn.setBackground(hoverBg); }
            @Override public void mouseExited(MouseEvent e)  { btn.setBackground(bg); }
        });
        return btn;
    }

    private JLabel makeLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(FONT_BODY);
        l.setForeground(TEXT_GREY);
        return l;
    }

    private JLabel makeBold(String text) {
        JLabel l = new JLabel(text);
        l.setFont(FONT_HEADING);
        l.setForeground(ACCENT_BLUE);
        return l;
    }

    private void styleTabPane(JTabbedPane pane) {
        pane.setUI(new javax.swing.plaf.basic.BasicTabbedPaneUI() {
            @Override protected void paintTabBackground(
                    Graphics g, int tabPlacement, int tabIndex,
                    int x, int y, int w, int h, boolean isSelected) {
                g.setColor(isSelected ? BG_CARD : BG_PANEL);
                g.fillRect(x, y, w, h);
            }
            @Override protected void paintTabBorder(
                    Graphics g, int tabPlacement, int tabIndex,
                    int x, int y, int w, int h, boolean isSelected) {
                if (isSelected) {
                    g.setColor(ACCENT_BLUE);
                    g.fillRect(x, y + h - 3, w, 3);
                }
            }
            @Override protected void paintFocusIndicator(
                    Graphics g, int tabPlacement, Rectangle[] rects,
                    int tabIndex, Rectangle iconRect, Rectangle textRect, boolean isSelected) {}
        });
        pane.setBorder(new EmptyBorder(0, 0, 0, 0));
        pane.setBackground(BG_PANEL);
    }

    private void setStatus(String msg) { statusLabel.setText(msg); }
    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error",   JOptionPane.ERROR_MESSAGE);
    }
    private void showInfo(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    // ============================================================
    //  Main Entry Point
    // ============================================================
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception ignored) {}

        UIManager.put("OptionPane.background",        BG_PANEL);
        UIManager.put("Panel.background",             BG_PANEL);
        UIManager.put("OptionPane.messageForeground", TEXT_WHITE);
        UIManager.put("Button.background",            BG_CARD);
        UIManager.put("Button.foreground",            TEXT_WHITE);

        // Launch on the Swing Event Dispatch Thread (EDT) — best practice
        SwingUtilities.invokeLater(() -> new ShoppingCartApp());
    }
}
