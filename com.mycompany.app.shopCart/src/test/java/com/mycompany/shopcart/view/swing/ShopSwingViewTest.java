package com.mycompany.shopcart.view.swing;

import org.assertj.swing.annotation.GUITest;
import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.Test;
import org.junit.runner.RunWith;

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

}
