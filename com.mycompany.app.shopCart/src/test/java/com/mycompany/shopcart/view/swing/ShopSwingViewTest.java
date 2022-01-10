package com.mycompany.shopcart.view.swing;

import static org.assertj.core.api.Assertions.assertThat;

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

import com.mycompany.shopcart.model.Product;

@RunWith(GUITestRunner.class)
public class ShopSwingViewTest extends AssertJSwingJUnitTestCase {

	private FrameFixture window;

	private ShopSwingView shopSwingView;

	@Override
	protected void onSetUp() {
		GuiActionRunner.execute(() -> {
			shopSwingView = new ShopSwingView();
			return shopSwingView;
		});
		window = new FrameFixture(robot(), shopSwingView);
		window.show();
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
		GuiActionRunner.execute(
				() -> shopSwingView.showAllProducts(
						Arrays.asList(product1, product2))
		);
		String[] listContents = window.list("productList").contents();
		assertThat(listContents)
			.containsExactly(product1.toString(), product2.toString());
	}
	
	@Test
	public void testShowErrorShouldShowTheMessageInTheErrorLabel() {
		Product product = new Product("1", "test1");
		GuiActionRunner.execute(
			() -> shopSwingView.showError("error message", product)
		);
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
		GuiActionRunner.execute(
				() -> shopSwingView.showErrorProductNotFound("error message", product1)
		);
		window.label("errorMessageLabel")
			.requireText("error message: " + product1.getName());
		assertThat(window.list("cartList").contents())
			.containsExactly(product2.toString());
		
	}
	
}
