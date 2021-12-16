package com.mycompany.shopcart.controller;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.ignoreStubs;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.mycompany.shopcart.model.Product;
import com.mycompany.shopcart.repository.ProductRepository;
import com.mycompany.shopcart.view.ProductView;

public class ShopControllerTest {
	
	@Mock
	private ProductRepository productRepository;
	
	@Mock
	private ProductView productView;
	
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
	public void testAllProducts () {
		List<Product> products = asList(new Product());
		when(productRepository.findAll())
			.thenReturn(products);
		shopController.allProducts();
		verify(productView).showAllProducts(products);
	}
	
	@Test
	public void testBuyProductWhenProductIsInDatabase() {
		Product productToBuy = new Product("1", "test");
		when(productRepository.findById("1")).thenReturn(productToBuy);
		shopController.buyProduct(productToBuy);
		verify(productView).addProductToCart(productToBuy);

	}
	
	@Test
	public void testBuyProductWhenProductIsNotInDatabase() {
		Product productToBuy = new Product("1", "test");
		when(productRepository.findById("1")).thenReturn(null);
		shopController.buyProduct(productToBuy);
		verify(productView).showError("The product you are trying to buy is no longer available");
		verifyNoMoreInteractions(ignoreStubs(productRepository));
	}
	
	@Test
	public void testRemoveProduct() {
		Product productToRemove = new Product("1", "test");
		when(productRepository.findById("1")).thenReturn(productToRemove);
		shopController.removeProduct(productToRemove);
		verify(productView).removeProductFromCart(productToRemove);
	}

}
