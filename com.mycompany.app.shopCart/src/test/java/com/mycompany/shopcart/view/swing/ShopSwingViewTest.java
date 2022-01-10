package com.mycompany.shopcart.view.swing;

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
	
	@Test
	public void test() {
		// Just to check the setup works
	}

}
