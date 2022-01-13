package com.mycompany.shopcart.view.swing;

import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListSelectionModel;
import javax.swing.WindowConstants;
import javax.swing.JScrollPane;
import javax.swing.DefaultListModel;
import javax.swing.JButton;

import com.mycompany.shopcart.controller.ShopController;
import com.mycompany.shopcart.model.Product;
import com.mycompany.shopcart.view.ProductView;

public class ShopSwingView extends JFrame implements ProductView {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private JPanel contentPane;
	private JButton btnBuySelected;
	private JList<Product> listProducts;
	private JList<Product> listCart;
	private JButton btnRemoveSelected;
	private JButton btnCheckout;
	private JLabel lblErrorMessage;
	
	private DefaultListModel<Product> listProductsModel;
	private DefaultListModel<Product> listCartModel;
	
	private transient ShopController shopController;

	DefaultListModel<Product> getListCartModel() {
		return listCartModel;
	}

	DefaultListModel<Product> getListProductsModel() {
		return listProductsModel;
	}
	
	public void setShopController(ShopController shopController) {
		this.shopController = shopController;
	}

	/**
	 * Create the frame.
	 */
	public ShopSwingView() {
		setTitle("Shop View");
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[]{0, 0, 0, 0, 0};
		gbl_contentPane.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0, 0};
		gbl_contentPane.columnWeights = new double[]{1.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		gbl_contentPane.rowWeights = new double[]{0.0, 1.0, 0.0, 0.0, 1.0, 0.0, 0.0, Double.MIN_VALUE};
		contentPane.setLayout(gbl_contentPane);
		
		JLabel lblShop = new JLabel("Shop");
		GridBagConstraints gbc_lblShop = new GridBagConstraints();
		gbc_lblShop.gridwidth = 4;
		gbc_lblShop.insets = new Insets(0, 0, 5, 0);
		gbc_lblShop.gridx = 0;
		gbc_lblShop.gridy = 0;
		contentPane.add(lblShop, gbc_lblShop);
		
		JScrollPane scrollPane = new JScrollPane();
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.gridwidth = 4;
		gbc_scrollPane.insets = new Insets(0, 0, 5, 0);
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.gridx = 0;
		gbc_scrollPane.gridy = 1;
		contentPane.add(scrollPane, gbc_scrollPane);
		
		listProductsModel = new DefaultListModel<>();
		listProducts = new JList<>(listProductsModel);
		listProducts.addListSelectionListener(
				e -> btnBuySelected.setEnabled(listProducts.getSelectedIndex() != -1));
		scrollPane.setViewportView(listProducts);
		listProducts.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		listProducts.setName("productList");
		
		btnBuySelected = new JButton("Buy Selected");
		btnBuySelected.setEnabled(false);
		btnBuySelected.addActionListener(
				e -> shopController.buyProduct(listProducts.getSelectedValue()));
		GridBagConstraints gbc_btnBuySelected = new GridBagConstraints();
		gbc_btnBuySelected.gridwidth = 4;
		gbc_btnBuySelected.insets = new Insets(0, 0, 5, 0);
		gbc_btnBuySelected.anchor = GridBagConstraints.EAST;
		gbc_btnBuySelected.gridx = 0;
		gbc_btnBuySelected.gridy = 2;
		contentPane.add(btnBuySelected, gbc_btnBuySelected);
		
		JLabel lblCart = new JLabel("Cart");
		GridBagConstraints gbc_lblCart = new GridBagConstraints();
		gbc_lblCart.gridwidth = 4;
		gbc_lblCart.insets = new Insets(0, 0, 5, 0);
		gbc_lblCart.gridx = 0;
		gbc_lblCart.gridy = 3;
		contentPane.add(lblCart, gbc_lblCart);
		
		JScrollPane scrollPane_1 = new JScrollPane();
		GridBagConstraints gbc_scrollPane_1 = new GridBagConstraints();
		gbc_scrollPane_1.gridwidth = 4;
		gbc_scrollPane_1.insets = new Insets(0, 0, 5, 0);
		gbc_scrollPane_1.fill = GridBagConstraints.BOTH;
		gbc_scrollPane_1.gridx = 0;
		gbc_scrollPane_1.gridy = 4;
		contentPane.add(scrollPane_1, gbc_scrollPane_1);
		
		listCartModel = new DefaultListModel<>();
		listCart = new JList<>(listCartModel);
		listCart.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		listCart.addListSelectionListener(
				e -> btnRemoveSelected.setEnabled(listCart.getSelectedIndex() != -1));
		listCart.addListSelectionListener(
				e -> btnCheckout.setEnabled(listCart.getSelectedIndex() != -1));
		scrollPane_1.setViewportView(listCart);
		listCart.setName("cartList");
		
		btnRemoveSelected = new JButton("Remove Selected");
		btnRemoveSelected.setEnabled(false);
		btnRemoveSelected.addActionListener(
				e -> shopController.removeProduct(listCart.getSelectedValue()));
		GridBagConstraints gbc_btnRemoveSelected = new GridBagConstraints();
		gbc_btnRemoveSelected.anchor = GridBagConstraints.EAST;
		gbc_btnRemoveSelected.insets = new Insets(0, 0, 5, 5);
		gbc_btnRemoveSelected.gridx = 0;
		gbc_btnRemoveSelected.gridy = 5;
		contentPane.add(btnRemoveSelected, gbc_btnRemoveSelected);
		
		btnCheckout = new JButton("Checkout");
		btnCheckout.setEnabled(false);
		btnCheckout.addActionListener(
				e -> shopController.checkoutProduct(listCart.getSelectedValue()));
		GridBagConstraints gbc_btnNewButton = new GridBagConstraints();
		gbc_btnNewButton.gridwidth = 2;
		gbc_btnNewButton.insets = new Insets(0, 0, 5, 0);
		gbc_btnNewButton.gridx = 2;
		gbc_btnNewButton.gridy = 5;
		contentPane.add(btnCheckout, gbc_btnNewButton);
		
		lblErrorMessage = new JLabel(" ");
		lblErrorMessage.setName("errorMessageLabel");
		GridBagConstraints gbc_label = new GridBagConstraints();
		gbc_label.gridwidth = 4;
		gbc_label.insets = new Insets(0, 0, 0, 5);
		gbc_label.gridx = 0;
		gbc_label.gridy = 6;
		contentPane.add(lblErrorMessage, gbc_label);
	}

	@Override
	public void showAllProducts(List<Product> products) {
		products.stream().forEach(listProductsModel::addElement);	
	}

	@Override
	public void addProductToCart(Product product) {
		listProductsModel.removeElement(product);
		listCartModel.addElement(product);
		resetErrorLabel();
	}

	@Override
	public void removeProductFromCart(Product product) {
		listCartModel.removeElement(product);
		listProductsModel.addElement(product);
		resetErrorLabel();
	}

	@Override
	public void showError(String message, Product product) {
		lblErrorMessage.setText(message + ": " + product.getName() );
	}

	@Override
	public void checkoutProduct(Product product) {
		listCartModel.removeElement(product);
		resetErrorLabel();
	}
	
	@Override
	public void showErrorProductNotFound(String message, Product product) {
		lblErrorMessage.setText(message + ": " + product.getName());
		listCartModel.removeElement(product);
	}
	
	@Override
	public void removeProductFromShop(Product productToBuy) {
		listProductsModel.removeElement(productToBuy);
	}
	
	private void resetErrorLabel() {
		lblErrorMessage.setText(" ");
		
	}

}
