package com.mycompany.shopcart.view.swing;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

import java.util.Arrays;

import javax.swing.DefaultListModel;

import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.fixture.JButtonFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.mycompany.shopcart.controller.ShopController;
import com.mycompany.shopcart.model.Product;

@RunWith(GUITestRunner.class)
public class ShopSwingViewTest extends AssertJSwingJUnitTestCase {

	private FrameFixture window;

	private ShopSwingView shopSwingView;
	
	@Mock
	private ShopController shopController;
	
	private AutoCloseable closeable;

	@Override
	protected void onSetUp() {
		closeable = MockitoAnnotations.openMocks(this);
		GuiActionRunner.execute(() -> {
			shopSwingView = new ShopSwingView();
			shopSwingView.setShopController(shopController);
			return shopSwingView;
		});
		window = new FrameFixture(robot(), shopSwingView);
		window.show();
	}
	
	@Override
	protected void onTearDown() throws Exception {
		closeable.close();
	}

	
	@Test @GUITest
	public void testControlsInitialStates() {
		window.list("productList");
		window.button(JButtonMatcher.withText("Buy Selected")).requireDisabled();
		window.list("cartList");
		window.button(JButtonMatcher.withText("Remove Selected")).requireDisabled();
		window.button(JButtonMatcher.withText("Checkout")).requireDisabled();
		window.label("errorMessageLabel").requireText(" ");
	}
	
	@Test
	public void testBuyButtonShouldBeEnabledOnlyWhenAProductIsSelectedInShop() {
		GuiActionRunner.execute(() -> shopSwingView.getListProductsModel().addElement(new Product("1", "test")));
		window.list("productList").selectItem(0);
		JButtonFixture buyButton = 
				window.button(JButtonMatcher.withText("Buy Selected"));
		buyButton.requireEnabled();
		window.list("productList").clearSelection();
		buyButton.requireDisabled();
	}
	
	@Test
	public void testRemoveButtonShouldBeEnabledOnlyWhenAProductIsSelectedInCart() {
		GuiActionRunner.execute(() -> shopSwingView.getListCartModel().addElement(new Product("1", "test")));
		window.list("cartList").selectItem(0);
		JButtonFixture removeButton = 
				window.button(JButtonMatcher.withText("Remove Selected"));
		removeButton.requireEnabled();
		window.list("cartList").clearSelection();
		removeButton.requireDisabled();
	}
	
	@Test
	public void testCheckoutButtonShouldBeEnabledOnlyWhenAProductIsSelectedInCart() {
		GuiActionRunner.execute(() -> shopSwingView.getListCartModel().addElement(new Product("1", "test")));
		window.list("cartList").selectItem(0);
		JButtonFixture checkoutButton = 
				window.button(JButtonMatcher.withText("Checkout"));
		checkoutButton.requireEnabled();
		window.list("cartList").clearSelection();
		checkoutButton.requireDisabled();
	}
	
	@Test
	public void testsShowAllProductsShouldAddProductDescriptionToProductsList() {
		Product product1 = new Product("1", "test1");
		Product product2 = new Product("2", "test2");
		shopSwingView.showAllProducts(
						Arrays.asList(product1, product2));
		String[] listContents = window.list("productList").contents();
		assertThat(listContents)
			.containsExactly(product1.toString(), product2.toString());
	}
	
	@Test
	public void testShowErrorShouldShowTheMessageInTheErrorLabel() {
		Product product = new Product("1", "test1");
		shopSwingView.showError("error message", product);
		window.label("errorMessageLabel")
			.requireText("error message: " + product.getName());
	}
	
	@Test
	public void testShowErrorProductNotFoundWhenCheckout() {
		Product product1 = new Product("1", "test1");
		Product product2 = new Product("2", "test2");
		GuiActionRunner.execute(
				() -> {
					DefaultListModel<Product> listCartModel = shopSwingView.getListCartModel();
					listCartModel.addElement(product1);
					listCartModel.addElement(product2);
				}
		);
		shopSwingView.showErrorProductNotFound("error message", product1);
		window.label("errorMessageLabel")
			.requireText("error message: " + product1.getName());
		assertThat(window.list("cartList").contents())
			.containsExactly(product2.toString());
		
	}
	
	@Test
	public void testAddProductToCartShouldMoveTheProductFromProductListToCartListAndResetTheErrorLabel() {
		Product product1 = new Product("1", "test1");
		Product product2 = new Product("2", "test2");
		GuiActionRunner.execute(
				() -> {
					DefaultListModel<Product> listProductsModel = shopSwingView.getListProductsModel();
					listProductsModel.addElement(product1);
					listProductsModel.addElement(product2);
				}
				);
		shopSwingView.addProductToCart(product1);
		String[] productListContents = window.list("productList").contents();
		assertThat(productListContents).containsExactly(product2.toString());
		String[] cartListContents = window.list("cartList").contents();
		assertThat(cartListContents).containsExactly(product1.toString());
		window.label("errorMessageLabel").requireText(" ");
	}
	
	@Test
	public void testRemoveProductFromCartShouldMoveTheProductFromCartListToProductListAndResetTheErrorLabel() {
		Product product1 = new Product("1", "test1");
		Product product2 = new Product("2", "test2");
		GuiActionRunner.execute(
				() -> {
					DefaultListModel<Product> listCartModel = shopSwingView.getListCartModel();
					listCartModel.addElement(product1);
					listCartModel.addElement(product2);
				}
				);
		shopSwingView.removeProductFromCart(product1);
		String[] productListContents = window.list("productList").contents();
		assertThat(productListContents).containsExactly(product1.toString());
		String[] cartListContents = window.list("cartList").contents();
		assertThat(cartListContents).containsExactly(product2.toString());
		window.label("errorMessageLabel").requireText(" ");
	}
	

	@Test
	public void testCheckoutProductShouldRemoveTheProductFromTheCartListAndResetTheErrorLabel() {
		Product product1 = new Product("1", "test1");
		Product product2 = new Product("2", "test2");
		GuiActionRunner.execute(
				() -> {
					DefaultListModel<Product> listCartModel = shopSwingView.getListCartModel();
					listCartModel.addElement(product1);
					listCartModel.addElement(product2);
				}
		);
		shopSwingView.checkoutProduct(new Product("1", "test1"));
		String[] listContents = window.list("cartList").contents();
		assertThat(listContents).containsExactly(product2.toString());
		window.label("errorMessageLabel").requireText(" ");
	}
	
	@Test
	public void testRemoveProductFromShopShouldRemoveTheProductFromProductList() {
		Product product1 = new Product("1", "test1");
		Product product2 = new Product("2", "test2");
		GuiActionRunner.execute(
				() -> {
					DefaultListModel<Product> listProductsModel = shopSwingView.getListProductsModel();
					listProductsModel.addElement(product1);
					listProductsModel.addElement(product2);
				}
				);
		shopSwingView.removeProductFromShop(new Product("1", "test1"));
		String[] listContents = window.list("productList").contents();
		assertThat(listContents).containsExactly(product2.toString());
	}
	
	@Test
	public void testAddButtonShouldDelegateToShopControllerBuyProduct() {
		Product product1 = new Product("1", "test1");
		Product product2 = new Product("2", "test2");
		GuiActionRunner.execute(
				() -> {
					DefaultListModel<Product> listProductsModel = shopSwingView.getListProductsModel();
					listProductsModel.addElement(product1);
					listProductsModel.addElement(product2);
				}
				);
		window.list("productList").selectItem(1);
		window.button(JButtonMatcher.withText("Buy Selected")).click();
		verify(shopController).buyProduct(product2);
	}
	
	@Test
	public void testRemoveButtonShouldDelegateToShopControllerRemoveProduct() {
		Product product1 = new Product("1", "test1");
		Product product2 = new Product("2", "test2");
		GuiActionRunner.execute(
				() -> {
					DefaultListModel<Product> listCartModel = shopSwingView.getListCartModel();
					listCartModel.addElement(product1);
					listCartModel.addElement(product2);
				}
				);
		window.list("cartList").selectItem(1);
		window.button(JButtonMatcher.withText("Remove Selected")).click();
		verify(shopController).removeProduct(product2);
	}
	
	@Test
	public void testCheckoutButtonShouldDelegateToShopControllerCheckoutProduct() {
		Product product1 = new Product("1", "test1");
		Product product2 = new Product("2", "test2");
		GuiActionRunner.execute(
				() -> {
					DefaultListModel<Product> listCartModel = shopSwingView.getListCartModel();
					listCartModel.addElement(product1);
					listCartModel.addElement(product2);
				}
				);
		window.list("cartList").selectItem(1);
		window.button(JButtonMatcher.withText("Checkout")).click();
		verify(shopController).checkoutProduct(product2);
	}
}
