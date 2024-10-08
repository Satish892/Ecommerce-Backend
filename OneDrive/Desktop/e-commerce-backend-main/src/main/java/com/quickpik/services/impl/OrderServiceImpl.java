package com.quickpik.services.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import com.quickpik.repositories.ProductRepository;
import com.quickpik.exception.ResourceNotFoundException;
import com.quickpik.helper.Helper;
import com.quickpik.dtos.CreateOrderRequest;
import com.quickpik.dtos.OrderDto;
import com.quickpik.dtos.PageableResponse;
import com.quickpik.dtos.UpdateOrderRequest;
import com.quickpik.entities.Cart;
import com.quickpik.entities.CartItem;
import com.quickpik.entities.Order;
import com.quickpik.entities.OrderItem;
import com.quickpik.entities.Product;
import com.quickpik.entities.User;
import com.quickpik.exception.BadApiRequestException;
import com.quickpik.repositories.CartRepository;
import com.quickpik.repositories.OrderRepository;
import com.quickpik.repositories.UserRepository;
import com.quickpik.services.OrderService;

@Service
public class OrderServiceImpl implements OrderService {

	@Autowired
	private OrderRepository orderRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private CartRepository cartRepository;

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private ModelMapper modelMapper;

	@Override
	public PageableResponse<OrderDto> getAllOrders(int pageNumber, int pageSize, String sortBy, String sortDir) {
		Sort sort = (sortDir.equalsIgnoreCase("desc")) ? (Sort.by(sortBy).descending())
				: (Sort.by(sortBy).descending());
		Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
		Page<Order> page = this.orderRepository.findAll(pageable);
		return Helper.getPageableResponse(page, OrderDto.class);
	}

	@Override
	public OrderDto getOrderById(String orderId) {
	    Order order = this.orderRepository.findById(orderId)
	            .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + orderId));
	    return modelMapper.map(order, OrderDto.class);
	}

	@Override
	public List<OrderDto> getOrdersByUser(String userId) {
	    User user = this.userRepository.findById(userId)
	            .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
	    List<Order> orders = this.orderRepository.findByUser(user);
	    List<OrderDto> orderDto = orders.stream()
	            .map(order -> modelMapper.map(order, OrderDto.class))
	            .collect(Collectors.toList());
	    return orderDto;
	}

	@Override
	public OrderDto createOrder(CreateOrderRequest orderRequest) {
		// Get user from the database based on userId
		User user = this.userRepository.findById(orderRequest.getUserId())
				.orElseThrow(() -> new ResourceNotFoundException("User Not Found"));

		// Get cart from the database based on cartId
		Cart cart = this.cartRepository.findById(orderRequest.getCartId())
		        .orElseThrow(() -> new ResourceNotFoundException("Cart Not Found"));


		// Get items from the cart
		List<CartItem> cartItems = cart.getItems();

		// If there are no items in the cart, throw an exception
		if (cartItems.size() <= 0) {
			throw new BadApiRequestException("No items found in cart");
		}

		// Generate a unique orderId and orderNumber
		String orderId = UUID.randomUUID().toString();
		String orderNumber = "ORD-" + System.currentTimeMillis() / 1000L + "-" + new Random().nextInt(1000);
		AtomicReference<Double> totalOrderAmount = new AtomicReference<Double>((double) 0);

		Order order = Order.builder().orderId(orderId).orderNumber(orderNumber).orderName(orderRequest.getOrderName())
				.shippingPhone(orderRequest.getShippingPhone()).orderStatus(orderRequest.getOrderStatus())
				.paymentStatus(orderRequest.getPaymentStatus()).shippingAddress(orderRequest.getShippingAddress())
				.city(orderRequest.getCity()).province(orderRequest.getProvince())
				.postalCode(orderRequest.getPostalCode()).user(user).build();

		// order items, amount to be set
		List<OrderItem> orderItems = new ArrayList<>();

		for (CartItem cartItem : cartItems) {
			Product product = cartItem.getProduct();
			int requestedQuantity = cartItem.getQuantity();
			int availableQuantity = product.getQuantity();

			if (requestedQuantity > availableQuantity) {
				// Skip adding this item to the order
				continue;
			}

			// cart item -> order item
			OrderItem orderItem = OrderItem.builder().quantity(requestedQuantity).product(product)
					.totalPrice(requestedQuantity * (product.getDiscountedPrice() != 0 ? product.getDiscountedPrice()
							: product.getUnitPrice()))
					.order(order).build();

			// Update the totalOrderAmount by adding the price of the OrderItem
			totalOrderAmount.set(totalOrderAmount.get() + orderItem.getTotalPrice());

			// Decrease product quantity by the ordered amount
			product.setQuantity(availableQuantity - requestedQuantity);
			this.productRepository.save(product);

			orderItems.add(orderItem);
		}

		// If no order items were added, throw an exception
		if (orderItems.isEmpty()) {
			throw new BadApiRequestException("Insufficient stock! No items available for order");
		}

		// set order items and order amount
		order.setOrderItems(orderItems);
		order.setOrderAmount(totalOrderAmount.get());

		// Clear cart after order is made
		cart.getItems().clear();
		this.cartRepository.save(cart);

		Order savedOrder = this.orderRepository.save(order);

		// Update stock field for products with quantity 0
		for (OrderItem orderItem : orderItems) {
			Product product = orderItem.getProduct();
			int remainingQuantity = product.getQuantity();

			if (remainingQuantity == 0) {
				product.setStock(false);
				this.productRepository.save(product);
			}
		}

		return modelMapper.map(savedOrder, OrderDto.class);

	}
	@Override
	public void removeOrder(String orderId) {
	    Order order = this.orderRepository.findById(orderId)
	            .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + orderId));

	    this.orderRepository.delete(order);
	}

	@Override
	public OrderDto updateOrder(String orderId, UpdateOrderRequest orderRequest) {
		Order order = this.orderRepository.findById(orderId)
				.orElseThrow(() -> new ResourceNotFoundException("Order not found"));
		order.setOrderStatus(orderRequest.getOrderStatus());
		order.setPaymentStatus(orderRequest.getPaymentStatus());
		order.setOrderName(orderRequest.getOrderName());
		order.setShippingPhone(orderRequest.getShippingPhone());
		order.setShippingAddress(orderRequest.getShippingAddress());
		order.setCity(orderRequest.getCity());
		order.setProvince(orderRequest.getProvince());
		order.setPostalCode(orderRequest.getPostalCode());
		order.setDeliveredDate(orderRequest.getDeliveredDate());
		this.orderRepository.save(order);
		return modelMapper.map(order, OrderDto.class);
	}

}