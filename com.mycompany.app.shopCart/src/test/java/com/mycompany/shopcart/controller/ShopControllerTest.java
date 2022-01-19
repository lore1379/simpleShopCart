package com.mycompany.shopcart.controller;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.mongodb.MongoWriteException;
import com.mongodb.client.ClientSession;
import com.mycompany.shopcart.model.Product;
import com.mycompany.shopcart.repository.ProductRepository;
import com.mycompany.shopcart.view.ShopView;

public class ShopControllerTest {

	@Mock
	private ProductRepository productRepository;

	@Mock
	private ShopView shopView;

	@InjectMocks
	private ShopController shopController;

	private AutoCloseable closeable;

	@Before
	public void setup() {
		closeable = MockitoAnnotations.openMocks(this);
	}

	@After
	public void releaseMocks() throws Exception {
		closeable.close();
	}

	@Test
	public void testAllProducts() {
		List<Product> products = asList(new Product());
		when(productRepository.findAll()).thenReturn(products);
		shopController.allProducts();
		verify(shopView).showAllProducts(products);
	}

	@Test
	public void testBuyProductWhenProductIsInDatabase() {
		Product productToBuy = new Product("1", "test");
		when(productRepository.findById("1")).thenReturn(productToBuy);
		shopController.buyProduct(productToBuy);
		verify(shopView).addProductToCart(productToBuy);

	}

	@Test
	public void testBuyProductWhenProductIsNotInDatabase() {
		Product productToBuy = new Product("1", "test");
		when(productRepository.findById("1")).thenReturn(null);
		shopController.buyProduct(productToBuy);
		verify(shopView).showError("The product you are trying to buy is no longer available", productToBuy);
		verify(shopView).removeProductFromShop(productToBuy);
		verifyNoMoreInteractions(ignoreStubs(productRepository));
	}

	@Test
	public void testRemoveProduct() {
		Product productToRemove = new Product("1", "test");
		shopController.removeProduct(productToRemove);
		verify(shopView).removeProductFromCart(productToRemove);
	}

	@Test
	public void testCheckoutProductWhenProductIsInDatabase() {
		ClientSession sessionMock = Mockito.mock(ClientSession.class);
		Product productInCart = new Product("1", "test1");
		when(productRepository.getNewClientSession()).thenReturn(sessionMock);
		when(productRepository.findById("1")).thenReturn(productInCart);
		shopController.checkoutProduct(productInCart);
		InOrder inOrder = inOrder(productRepository, shopView);
		inOrder.verify(productRepository).delete(sessionMock, "1");
		inOrder.verify(shopView).checkoutProduct(productInCart);
	}

	@Test
	public void testCheckoutProductWhenProductIsNotInDatabase() {
		ClientSession sessionMock = Mockito.mock(ClientSession.class);
		Product productInCart = new Product("1", "test");
		when(productRepository.getNewClientSession()).thenReturn(sessionMock);
		when(productRepository.findById("1")).thenReturn(null);
		shopController.checkoutProduct(productInCart);
		verify(shopView).showErrorProductNotFound("The product you are trying to buy is no longer available",
				productInCart);
		verifyNoMoreInteractions(ignoreStubs(productRepository));
	}
	
	@Test
	public void testCheckoutProductWhenExceptionThrownInTransaction() {
		ClientSession sessionMock = Mockito.mock(ClientSession.class);
		Product productInCart = new Product("1", "test");
		when(productRepository.getNewClientSession()).thenReturn(sessionMock);
		when(productRepository.findById("1")).thenReturn(productInCart);
		doThrow(MongoWriteException.class).when(productRepository).delete(sessionMock, "1");
		shopController.checkoutProduct(productInCart);
		verify(shopView).showErrorProductNotFound("The product you are trying to buy is no longer available",
				productInCart);
		verifyNoMoreInteractions(ignoreStubs(productRepository));
	}
	
}
