<div align="center">

<!-- ══════════════════════════════════════════════════════════════════════════════ -->
<!--                     UDAY — BACKEND ENGINEER PROFILE README                     -->
<!-- ══════════════════════════════════════════════════════════════════════════════ -->

<img src="https://capsule-render.vercel.app/api?type=venom&color=0:050816,35:0f172a,70:1d4ed8,100:00f2fe&height=310&section=header&text=UDAY%20%E2%80%A2%20BACKEND%20ENGINEER&fontSize=50&fontColor=ffffff&fontAlignY=38&stroke=00f2fe&strokeWidth=1.2&animation=twinkling&desc=%E2%9A%A1%20Java%2021%20%C2%B7%20Spring%20Boot%203%20%C2%B7%20Apache%20Kafka%20%C2%B7%20Distributed%20Microservices&descSize=19&descAlignY=60" width="100%" alt="Uday — Backend Engineer Banner" />

<a href="https://github.com/UDAY1810">
  <img src="https://readme-typing-svg.demolab.com?font=JetBrains+Mono&weight=700&size=21&duration=2600&pause=850&color=00F2FE&center=true&vCenter=true&width=860&lines=%E2%98%95+Architecting+high-throughput%2C+fault-tolerant+Java+21+%26+Spring+Boot+3+backends;%E2%9A%A1+Event-driven+choreography+%26+async+saga+pipelines+over+Apache+Kafka;%F0%9F%A7%AD+Spring+Cloud+Eureka+%C2%B7+Reactive+WebFlux+Gateway+%C2%B7+Config+Server+%C2%B7+OpenFeign;%F0%9F%9B%A1%EF%B8%8F+Zero-trust+JWT+edge+auth+%C2%B7+Resilience4j+Circuit+Breakers+%C2%B7+Zipkin+Tracing;%F0%9F%97%84%EF%B8%8F+Database-per-service+PostgreSQL+schemas+%2B+Flyway+versioned+migrations;%F0%9F%8D%94+Flagship+System+%E2%86%92+QuickBite+(9-Microservice+Food+Ordering+%26+Delivery+Engine)" alt="Typing SVG" />
</a>

<br/>

<p align="center">
  <a href="mailto:mrdubey7717@gmail.com"><img src="https://img.shields.io/badge/Gmail-mrdubey7717%40gmail.com-EA4335?style=for-the-badge&logo=gmail&logoColor=white&labelColor=0d1117" alt="Email" /></a>
  <a href="https://github.com/UDAY1810"><img src="https://img.shields.io/badge/GitHub-UDAY1810-00F2FE?style=for-the-badge&logo=github&logoColor=white&labelColor=0d1117" alt="GitHub" /></a>
  <a href="https://www.linkedin.com/in/YOUR-LINKEDIN-ID/"><img src="https://img.shields.io/badge/LinkedIn-Connect-0A66C2?style=for-the-badge&logo=linkedin&logoColor=white&labelColor=0d1117" alt="LinkedIn" /></a>
  <a href="https://www.instagram.com/YOUR-INSTAGRAM-ID/"><img src="https://img.shields.io/badge/Instagram-Follow-E4405F?style=for-the-badge&logo=instagram&logoColor=white&labelColor=0d1117" alt="Instagram" /></a>
  <img src="https://komarev.com/ghpvc/?username=UDAY1810&style=for-the-badge&color=00f2fe&labelColor=0d1117&label=PROFILE+VIEWS" alt="Profile views" />
</p>

<p align="center">
  <img src="https://img.shields.io/badge/SYSTEM_STATUS-ONLINE_%26_SHIPPING-10B981?style=flat-square&logo=statuspage&logoColor=white&labelColor=0d1117" alt="System Status" />
  <img src="https://img.shields.io/badge/RUNTIME-JAVA_21_LTS_(VIRTUAL_THREADS)-ED8B00?style=flat-square&logo=openjdk&logoColor=white&labelColor=0d1117" alt="Java 21" />
  <img src="https://img.shields.io/badge/PARADIGM-EVENT--DRIVEN_MICROSERVICES-38BDF8?style=flat-square&logo=apachekafka&logoColor=white&labelColor=0d1117" alt="Event-Driven" />
  <img src="https://img.shields.io/badge/OBSERVABILITY-MICROMETER_%2B_ZIPKIN-F97316?style=flat-square&logo=prometheus&logoColor=white&labelColor=0d1117" alt="Observability" />
  <img src="https://img.shields.io/badge/OPEN_TO-BACKEND_%2F_SDE_ROLES-A855F7?style=flat-square&logo=rocket&logoColor=white&labelColor=0d1117" alt="Open To Roles" />
</p>

</div>

<img src="https://user-images.githubusercontent.com/74038190/212284100-561aa473-3905-4a80-b561-0d28506553ee.gif" width="100%" alt="divider" />

## <img src="https://media.giphy.com/media/hvRJCLFzcasrR4ia7z/giphy.gif" width="28" alt="wave" /> `whoami` — Backend Engineering Identity & Runtime Spec

```java
package dev.uday.architecture.profile;

import java.util.List;
import java.util.Map;

/**
 * Production Backend Engineer specializing in resilient, event-driven
 * Java 21 & Spring Boot 3 microservice ecosystems.
 */
public final record BackendEngineerProfile(
    String handle,
    String role,
    List<String> languages,
    Map<String, List<String>> architectureStack,
    String flagshipSystem,
    String engineeringMotto
) {
    public static BackendEngineerProfile initialize() {
        return new BackendEngineerProfile(
            "UDAY1810",
            "Backend & Distributed Systems Engineer",
            List.of("Java 21 (LTS)", "TypeScript", "SQL (PostgreSQL)", "JavaScript"),
            Map.of(
                "Core & Frameworks", List.of("Spring Boot 3.x", "Spring Cloud", "Spring Security (JWT)", "Spring Data JPA / Hibernate"),
                "Service Mesh & Edge", List.of("Spring Cloud Gateway (Reactive WebFlux)", "Netflix Eureka", "Spring Cloud Config", "OpenFeign"),
                "Event & Messaging",   List.of("Apache Kafka (Event Choreography)", "WebSocket / STOMP (Real-Time Push)"),
                "Fault Tolerance",     List.of("Resilience4j (Circuit Breaker, Retry, TimeLimiter)", "Idempotent Webhooks & Consumers"),
                "Data & Migrations",   List.of("PostgreSQL (Database-per-Service)", "Flyway Schema Versioning", "H2 In-Memory"),
                "Observability & QA",  List.of("Micrometer Tracing", "Zipkin Distributed Spans", "Spring Actuator", "JUnit 5", "Mockito")
            ),
            "QuickBite — 9-Microservice Cloud-Native Food Ordering & Delivery Platform",
            "Make it work, make it right, make it fast — and make it observable."
        );
    }
}
```

| Architectural Principle | How I Engineer It in Production Code |
| :--- | :--- |
| **🛡️ Zero-Trust Edge Security** | Stateless `Bearer` JWT verification at the reactive **Spring Cloud Gateway (WebFlux)** layer, propagating verified principal headers (`X-User-Id`, `X-User-Role`) to downstream services. |
| **⚡ Loose Coupling via Events** | State mutations in core domains (**Orders**, **Payments**) emit immutable domain events to **Apache Kafka**, allowing **Delivery** and **Notification** services to react asynchronously. |
| **🔁 Graceful Degradation** | Every synchronous inter-service **OpenFeign** call is wrapped in a **Resilience4j** `@CircuitBreaker` and `@Retry` policy so downstream latency spikes never cascade into system-wide outages. |
| **🗄️ Strict Data Ownership** | **Database-per-Service** isolation on **PostgreSQL** with automated, repeatable **Flyway** SQL migrations (`V1__init.sql`, `V2__indexes.sql`) and optimistic locking on concurrent entities. |
| **🔍 End-to-End Traceability** | Distributed `traceId` and `spanId` propagation across HTTP, Feign, and Kafka headers via **Micrometer Tracing + Zipkin**, paired with **Spring Boot Actuator** readiness/liveness probes. |

<img src="https://user-images.githubusercontent.com/74038190/212284100-561aa473-3905-4a80-b561-0d28506553ee.gif" width="100%" alt="divider" />

## 🛠️ Tech Arsenal & Engineering Stack

<div align="center">

<img src="https://skillicons.dev/icons?i=java,spring,hibernate,kafka,postgres,docker,maven,ts,angular,git,github,postman,idea,vscode&theme=dark&perline=7" alt="Tech Stack Icons" />

</div>

<br/>

<table>
  <thead>
    <tr>
      <th align="left" width="24%">Layer / Domain</th>
      <th align="left" width="76%">Technologies, Frameworks &amp; Production Tooling</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td><b>☕ Languages &amp; Core</b></td>
      <td>
        <img src="https://img.shields.io/badge/Java_21_LTS-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white" alt="Java 21" />
        <img src="https://img.shields.io/badge/TypeScript-3178C6?style=for-the-badge&logo=typescript&logoColor=white" alt="TypeScript" />
        <img src="https://img.shields.io/badge/SQL-336791?style=for-the-badge&logo=postgresql&logoColor=white" alt="SQL" />
        <img src="https://img.shields.io/badge/JavaScript-F7DF1E?style=for-the-badge&logo=javascript&logoColor=black" alt="JavaScript" />
        <img src="https://img.shields.io/badge/HTML5-E34F26?style=for-the-badge&logo=html5&logoColor=white" alt="HTML5" />
        <img src="https://img.shields.io/badge/CSS3-1572B6?style=for-the-badge&logo=css3&logoColor=white" alt="CSS3" />
      </td>
    </tr>
    <tr>
      <td><b>🌱 Spring Ecosystem</b></td>
      <td>
        <img src="https://img.shields.io/badge/Spring_Boot_3-6DB33F?style=for-the-badge&logo=springboot&logoColor=white" alt="Spring Boot" />
        <img src="https://img.shields.io/badge/Spring_Cloud-6DB33F?style=for-the-badge&logo=spring&logoColor=white" alt="Spring Cloud" />
        <img src="https://img.shields.io/badge/Spring_Security_JWT-6DB33F?style=for-the-badge&logo=springsecurity&logoColor=white" alt="Spring Security" />
        <img src="https://img.shields.io/badge/Reactive_Gateway-6DB33F?style=for-the-badge&logo=spring&logoColor=white" alt="API Gateway" />
        <img src="https://img.shields.io/badge/JPA_%2F_Hibernate-59666C?style=for-the-badge&logo=hibernate&logoColor=white" alt="Hibernate" />
      </td>
    </tr>
    <tr>
      <td><b>🧭 Distributed Cloud</b></td>
      <td>
        <img src="https://img.shields.io/badge/Netflix_Eureka-00F2FE?style=for-the-badge&logo=spring&logoColor=black" alt="Eureka" />
        <img src="https://img.shields.io/badge/Config_Server-38BDF8?style=for-the-badge&logo=spring&logoColor=black" alt="Config Server" />
        <img src="https://img.shields.io/badge/OpenFeign_REST-6DB33F?style=for-the-badge&logo=spring&logoColor=white" alt="OpenFeign" />
        <img src="https://img.shields.io/badge/Resilience4j-FF6F00?style=for-the-badge&logo=shield&logoColor=white" alt="Resilience4j" />
      </td>
    </tr>
    <tr>
      <td><b>⚡ Messaging &amp; Push</b></td>
      <td>
        <img src="https://img.shields.io/badge/Apache_Kafka-231F20?style=for-the-badge&logo=apachekafka&logoColor=white" alt="Kafka" />
        <img src="https://img.shields.io/badge/WebSocket_STOMP-00D2FF?style=for-the-badge&logo=socketdotio&logoColor=black" alt="WebSocket" />
        <img src="https://img.shields.io/badge/Razorpay_Webhooks-0C2451?style=for-the-badge&logo=razorpay&logoColor=white" alt="Razorpay" />
        <img src="https://img.shields.io/badge/Angular_21_SPA-DD0031?style=for-the-badge&logo=angular&logoColor=white" alt="Angular 21" />
      </td>
    </tr>
    <tr>
      <td><b>🗄️ Data, DevOps &amp; QA</b></td>
      <td>
        <img src="https://img.shields.io/badge/PostgreSQL-4169E1?style=for-the-badge&logo=postgresql&logoColor=white" alt="PostgreSQL" />
        <img src="https://img.shields.io/badge/Flyway_Migrations-CC0200?style=for-the-badge&logo=flyway&logoColor=white" alt="Flyway" />
        <img src="https://img.shields.io/badge/Docker_Compose-2496ED?style=for-the-badge&logo=docker&logoColor=white" alt="Docker" />
        <img src="https://img.shields.io/badge/Zipkin_Tracing-FE7139?style=for-the-badge&logo=prometheus&logoColor=white" alt="Zipkin" />
        <img src="https://img.shields.io/badge/JUnit_5_%2B_Mockito-25A162?style=for-the-badge&logo=junit5&logoColor=white" alt="JUnit 5" />
        <img src="https://img.shields.io/badge/Apache_Maven-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white" alt="Maven" />
      </td>
    </tr>
  </tbody>
</table>

<img src="https://user-images.githubusercontent.com/74038190/212284100-561aa473-3905-4a80-b561-0d28506553ee.gif" width="100%" alt="divider" />

## 💻 Inside My Backend Codebase — Production Design Patterns

> **Click any module below** to inspect the actual patterns I use across my microservices for **fault-tolerant RPC**, **event-driven Kafka choreography**, **reactive JWT edge security**, and **real-time WebSocket push**.

<details open>
<summary><b>🔁 1. Fault-Tolerant Synchronous RPC &amp; Event Publishing (OpenFeign + Resilience4j + Kafka)</b></summary>
<br/>

```java
package com.quickbite.order.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderOrchestrationService {

    private static final String ORDER_EVENTS_TOPIC = "quickbite.orders.lifecycle.v1";

    private final RestaurantFeignClient restaurantClient;
    private final OrderRepository orderRepository;
    private final KafkaTemplate<String, OrderCreatedEvent> kafkaTemplate;

    @Transactional
    @Retry(name = "restaurantCatalogRetry")
    @CircuitBreaker(name = "restaurantCatalogCB", fallbackMethod = "handleCatalogUnavailable")
    public OrderResponse placeOrder(String userId, CreateOrderRequest request) {
        // 1. Synchronous Feign validation guarded by Resilience4j Circuit Breaker
        MenuValidationResponse validation = restaurantClient.validateAndPriceItems(
            request.restaurantId(), request.items()
        );

        // 2. Persist order aggregate in Order Service's dedicated PostgreSQL schema
        OrderEntity savedOrder = orderRepository.save(
            OrderEntity.createPending(userId, request.restaurantId(), validation.totalAmount())
        );

        // 3. Publish immutable domain event to Apache Kafka partitioned by orderId
        OrderCreatedEvent event = OrderCreatedEvent.from(savedOrder, request.deliveryAddress());
        kafkaTemplate.send(ORDER_EVENTS_TOPIC, savedOrder.getId().toString(), event)
            .whenComplete((result, ex) -> {
                if (ex != null) {
                    log.error("Failed to publish OrderCreatedEvent for orderId={}", savedOrder.getId(), ex);
                } else {
                    log.info("Published OrderCreatedEvent [partition={}, offset={}]",
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset());
                }
            });

        return OrderResponse.from(savedOrder);
    }

    // Graceful fallback when Restaurant Service is degraded or circuit is OPEN
    public OrderResponse handleCatalogUnavailable(String userId, CreateOrderRequest req, Throwable t) {
        log.warn("CircuitBreaker OPEN for restaurantId={} — reason: {}", req.restaurantId(), t.getMessage());
        throw new ServiceTemporarilyUnavailableException(
            "Restaurant catalog is momentarily busy. Please retry your order in a few seconds."
        );
    }
}
```

</details>

<details>
<summary><b>🛡️ 2. Reactive API Gateway JWT Authentication Filter (Spring Cloud Gateway + WebFlux)</b></summary>
<br/>

```java
package com.quickbite.gateway.security;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class ReactiveJwtAuthenticationFilter implements GlobalFilter, Ordered {

    private final JwtTokenValidator jwtValidator;
    private final RouteValidator routeValidator;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        if (routeValidator.isSecured.test(request)) {
            String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }

            try {
                Claims claims = jwtValidator.validateAndExtractClaims(authHeader.substring(7));
                // Mutate downstream request with verified identity headers
                ServerHttpRequest mutatedRequest = request.mutate()
                    .header("X-Authenticated-User-Id", claims.getSubject())
                    .header("X-Authenticated-Roles", claims.get("roles", String.class))
                    .build();
                return chain.filter(exchange.mutate().request(mutatedRequest).build());
            } catch (Exception ex) {
                exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                return exchange.getResponse().setComplete();
            }
        }
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return -1; // Execute before route forwarding filters
    }
}
```

</details>

<details>
<summary><b>⚡ 3. Event Consumer + Real-Time WebSocket Dispatch (KafkaListener + STOMP Broker)</b></summary>
<br/>

```java
package com.quickbite.delivery.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentVerifiedKafkaConsumer {

    private final DeliveryDispatchService dispatchService;
    private final SimpMessagingTemplate stompBroker;

    @Transactional
    @KafkaListener(
        topics = "quickbite.payments.verified.v1",
        groupId = "delivery-dispatch-group"
    )
    public void onPaymentVerified(PaymentVerifiedEvent event) {
        log.info("Received PaymentVerifiedEvent for orderId={} [txId={}]",
            event.orderId(), event.razorpayPaymentId());

        // 1. Allocate nearest available rider & persist delivery assignment
        DeliveryAssignment assignment = dispatchService.assignRider(event.orderId(), event.dropLocation());

        // 2. Push real-time status frame to Angular 21 client over STOMP WebSocket
        LiveTrackingPayload payload = LiveTrackingPayload.of(
            event.orderId(),
            DeliveryStatus.RIDER_ASSIGNED,
            assignment.getRiderName(),
            assignment.getEstimatedArrivalMinutes()
        );
        stompBroker.convertAndSend("/topic/orders/" + event.orderId() + "/tracking", payload);
    }
}
```

</details>

<img src="https://user-images.githubusercontent.com/74038190/212284100-561aa473-3905-4a80-b561-0d28506553ee.gif" width="100%" alt="divider" />

## 🍔 Flagship Distributed System — [QuickBite](https://github.com/UDAY1810/quickbite)

<div align="center">

### **Cloud-Native Food Ordering & Real-Time Delivery Platform**
*A production-styled 9-microservice Spring Boot 3 & Java 21 platform with an Angular 21 SPA, Apache Kafka event backbone, Razorpay payments, STOMP WebSockets, and Zipkin distributed tracing.*

<p align="center">
  <a href="https://github.com/UDAY1810/quickbite"><img src="https://img.shields.io/badge/VIEW_QUICKBITE_REPOSITORY-00F2FE?style=for-the-badge&logo=github&logoColor=black&labelColor=00F2FE" alt="View QuickBite Repo" /></a>
  <img src="https://img.shields.io/badge/Java_21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white&labelColor=161b22" alt="Java 21" />
  <img src="https://img.shields.io/badge/9_Microservices-6DB33F?style=for-the-badge&logo=springboot&logoColor=white&labelColor=161b22" alt="9 Microservices" />
  <img src="https://img.shields.io/badge/Kafka_Event_Mesh-231F20?style=for-the-badge&logo=apachekafka&logoColor=white&labelColor=161b22" alt="Kafka" />
  <img src="https://img.shields.io/badge/Angular_21-DD0031?style=for-the-badge&logo=angular&logoColor=white&labelColor=161b22" alt="Angular 21" />
  <img src="https://img.shields.io/badge/Docker_Compose-2496ED?style=for-the-badge&logo=docker&logoColor=white&labelColor=161b22" alt="Docker" />
</p>

</div>

### 🗺️ System Architecture Topology

```mermaid
flowchart TB
    subgraph Client["🌐 Client Layer"]
        UI["🖥️ Angular 21 SPA<br/>(Reactive Store · JWT Interceptor · STOMP Client)"]
    end

    subgraph Edge["🛡️ Edge & Control Plane"]
        GW["⚡ Spring Cloud Gateway<br/>(WebFlux · JWT Auth Filter · Route Predicates)"]
        EUR["🧭 Netflix Eureka<br/>(Dynamic Service Registry)"]
        CFG["⚙️ Config Server<br/>(Centralized Git/Native Profiles)"]
        ZIP["🔍 Micrometer + Zipkin<br/>(Distributed Trace Collector)"]
    end

    subgraph Core["🧩 Domain Microservices (Java 21 + Spring Boot 3)"]
        USR["👤 User Service<br/>(Identity · BCrypt · JWT Issuance)"]
        RES["🍽️ Restaurant Service<br/>(Catalogs · Menus · Availability)"]
        ORD["🧾 Order Service<br/>(Order State Machine · OpenFeign + Resilience4j)"]
        PAY["💳 Payment Service<br/>(Razorpay Orders · HMAC Signature Verification)"]
        DEL["🛵 Delivery Service<br/>(Rider Allocation · STOMP WebSocket Broker)"]
        NOT["📧 Notification Service<br/>(Event-Driven Email & Push Alerts)"]
    end

    subgraph Backbone["⚡ Event & Persistence Layer"]
        K[("🔥 Apache Kafka Broker<br/>(orders.v1 · payments.v1 · delivery.v1)")]
        DB[("🐘 PostgreSQL + Flyway<br/>(Isolated Database-per-Service Schemas)")]
    end

    UI ==>|HTTPS REST + Bearer JWT| GW
    UI <-.->|Live Order Telemetry (STOMP / WS)| DEL

    GW --> USR & RES & ORD & PAY & DEL
    ORD -->|Sync Feign + CircuitBreaker| RES

    ORD -.->|Publish OrderCreated| K
    PAY -.->|Publish PaymentVerified| K
    K -.->|Consume Domain Events| DEL & NOT & ORD

    USR & RES & ORD & PAY & DEL --> DB
    Core -.->|Heartbeat & Lookup| EUR
    Core -.->|Fetch Config| CFG
    Core -.->|Export Trace Spans| ZIP

    classDef client fill:#0f172a,stroke:#38bdf8,stroke-width:2px,color:#fff
    classDef edge fill:#1e1b4b,stroke:#00f2fe,stroke-width:2px,color:#fff
    classDef domain fill:#052e16,stroke:#4ade80,stroke-width:1.5px,color:#fff
    classDef infra fill:#27120b,stroke:#fb923c,stroke-width:2px,color:#fff

    class UI client
    class GW,EUR,CFG,ZIP edge
    class USR,RES,ORD,PAY,DEL,NOT domain
    class K,DB infra
```

### 🔄 End-to-End Distributed Order Saga Sequence

```mermaid
sequenceDiagram
    autonumber
    actor C as 👤 Customer (Angular 21)
    participant GW as 🛡️ API Gateway
    participant ORD as 🧾 Order Service
    participant RES as 🍽️ Restaurant Service
    participant PAY as 💳 Payment Service (Razorpay)
    participant K as ⚡ Apache Kafka
    participant DEL as 🛵 Delivery Service (WS)
    participant NOT as 📧 Notification Service

    C->>GW: POST /api/v1/orders (Bearer JWT)
    GW->>GW: Validate JWT & inject X-Authenticated-User-Id
    GW->>ORD: Forward Order Placement Request
    ORD->>RES: OpenFeign validateAndPriceItems() [Resilience4j CB]
    RES-->>ORD: 200 OK (Verified Pricing & Availability)
    ORD->>ORD: Save Order (Status = PENDING_PAYMENT) + Flyway Schema
    ORD-->>C: 201 Created (orderId, payableAmount)
    C->>PAY: Initiate & Complete Razorpay Checkout
    PAY->>PAY: Verify HMAC-SHA256 Razorpay Signature
    PAY--)K: Publish PaymentVerifiedEvent (key = orderId)
    par Async Choreography Fan-Out
        K--)ORD: Transition Order -> CONFIRMED
    and
        K--)DEL: Allocate Rider & Start Delivery Lifecycle
    and
        K--)NOT: Dispatch Order & Payment Receipt
    end
    DEL-->>C: Push STOMP Frame (/topic/orders/{id}/tracking)
```

### 🗂️ Complete 9-Microservice Breakdown

| # | Microservice | Primary Responsibility | Key Stack & Patterns |Persistence / Broker |
| :-: | :--- | :--- | :--- | :--- |
| `01` | **🧭 Eureka Server** | Dynamic service registry & health heartbeats | Spring Cloud Netflix Eureka | In-Memory Registry |
| `02` | **⚙️ Config Server** | Centralized external configuration management | Spring Cloud Config | Git / Native Profiles |
| `03` | **🛡️ API Gateway** | Single edge entry point, routing & JWT security filter | Spring Cloud Gateway (WebFlux), JJWT | Stateless Reactive |
| `04` | **👤 User Service** | Registration, RBAC roles, login & JWT generation | Spring Security, BCrypt, JPA | PostgreSQL + Flyway |
| `05` | **🍽️ Restaurant Service** | Restaurant onboarding, menu catalogs & item pricing | Spring Boot REST, Bean Validation | PostgreSQL + Flyway |
| `06` | **🧾 Order Service** | Order lifecycle orchestration & sync catalog checks | OpenFeign, Resilience4j, Kafka Producer | PostgreSQL + Flyway |
| `07` | **💳 Payment Service** | Checkout creation, webhook verification & ledger | Razorpay SDK, HMAC-SHA256, Kafka Producer | PostgreSQL + Flyway |
| `08` | **🛵 Delivery Service** | Rider assignment, status state machine & live tracking | Kafka Consumer, WebSocket (STOMP) | PostgreSQL + Flyway |
| `09` | **📧 Notification Service** | Asynchronous customer & restaurant event alerts | Kafka Consumer, Spring Mail / Templates | Stateless Event Consumer |

<img src="https://user-images.githubusercontent.com/74038190/212284100-561aa473-3905-4a80-b561-0d28506553ee.gif" width="100%" alt="divider" />

## 📊 GitHub Telemetry & Contribution Pulse

<div align="center">

<img src="https://streak-stats.demolab.com/?user=UDAY1810&theme=tokyonight&hide_border=true&background=0D1117&ring=00F2FE&fire=38BDF8&currStreakLabel=00F2FE&sideLabels=38BDF8&dates=8B949E&border_radius=10" width="68%" alt="GitHub Streak" />

<br/><br/>

<img src="https://github-readme-stats.vercel.app/api?username=UDAY1810&show_icons=true&include_all_commits=true&hide=stars,prs,issues&theme=tokyonight&hide_border=true&bg_color=0D1117&title_color=00F2FE&icon_color=38BDF8&text_color=C9D1D9&ring_color=00F2FE&border_radius=10" height="175" alt="GitHub Stats" />
<img src="https://github-readme-stats.vercel.app/api/top-langs/?username=UDAY1810&layout=compact&langs_count=6&theme=tokyonight&hide_border=true&bg_color=0D1117&title_color=00F2FE&text_color=C9D1D9&border_radius=10" height="175" alt="Top Languages" />

<br/><br/>

<img src="https://github-readme-activity-graph.vercel.app/graph?username=UDAY1810&bg_color=0d1117&color=38bdf8&line=00f2fe&point=ffffff&area=true&hide_border=true&custom_title=UDAY1810%20%E2%80%94%20Contribution%20Activity%20Telemetry" width="96%" alt="Activity Graph" />

</div>

<!-- Requires the snake GitHub Action in UDAY1810/UDAY1810 (.github/workflows/snake.yml) -->
<div align="center">
  <picture>
    <source media="(prefers-color-scheme: dark)" srcset="https://raw.githubusercontent.com/UDAY1810/UDAY1810/output/github-snake-dark.svg" />
    <source media="(prefers-color-scheme: light)" srcset="https://raw.githubusercontent.com/UDAY1810/UDAY1810/output/github-snake.svg" />
    <img alt="Contribution snake" src="https://raw.githubusercontent.com/UDAY1810/UDAY1810/output/github-snake-dark.svg" width="100%" />
  </picture>
</div>

<img src="https://user-images.githubusercontent.com/74038190/212284100-561aa473-3905-4a80-b561-0d28506553ee.gif" width="100%" alt="divider" />

<div align="center">

### 🤝 Let's Architect High-Scale Backend Systems Together

*Open to **Backend Engineer / Software Development Engineer (Java · Spring Boot · Microservices · Kafka)** opportunities and technical collaborations.*

<a href="mailto:mrdubey7717@gmail.com"><img src="https://img.shields.io/badge/Email_Me-mrdubey7717%40gmail.com-00F2FE?style=for-the-badge&logo=gmail&logoColor=black&labelColor=00F2FE" alt="Email Me" /></a>
<a href="https://github.com/UDAY1810/quickbite"><img src="https://img.shields.io/badge/Star_Flagship_Project-%E2%AD%90_QuickBite-F59E0B?style=for-the-badge&logo=github&logoColor=black&labelColor=F59E0B" alt="Star QuickBite" /></a>

<br/><br/>

<img src="https://quotes-github-readme.vercel.app/api?type=horizontal&theme=tokyonight" alt="Dev Quote" />

<img src="https://capsule-render.vercel.app/api?type=waving&color=0:00f2fe,45:1d4ed8,100:050816&height=120&section=footer" width="100%" alt="Footer Wave" />

</div>
