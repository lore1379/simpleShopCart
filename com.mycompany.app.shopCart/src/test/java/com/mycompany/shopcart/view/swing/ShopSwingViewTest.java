package com.mycompany.shopcart.view.swing;

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
	public void testCheckoutButtonShouldBeEnabledOnlyWhenAtLeastOneProductIsInCart() {
		GuiActionRunner.execute(() -> shopSwingView.getListCartModel().addElement(new Product("1", "test")));
		JButtonFixture checkoutButton = 
				window.button(JButtonMatcher.withText("Checkout"));
		checkoutButton.requireEnabled();
		window.list("cartList").clearSelection();
		checkoutButton.requireDisabled();
	}

}
