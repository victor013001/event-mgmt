package co.com.techtest.dynamodb.repository;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.core.async.SdkPublisher;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbAsyncIndex;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbAsyncTable;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.enhanced.dynamodb.model.PagePublisher;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;

import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

@Slf4j
public abstract class GenericOperationsRepository<E, K, V> {
    private final Class<V> dataClass;
    private final Function<V, E> toDomainFn;
    private final Function<E, V> toDataFn;
    private final DynamoDbAsyncTable<V> table;
    private final DynamoDbAsyncIndex<V> tableByIndex;

    @SuppressWarnings("unchecked")
    protected GenericOperationsRepository(DynamoDbEnhancedAsyncClient dynamoDbEnhancedAsyncClient,
                                          Function<V, E> toDomainFn,
                                          Function<E, V> toDataFn,
                                          String tableName,
                                          String... index) {
        this.toDomainFn = toDomainFn;
        this.toDataFn = toDataFn;
        ParameterizedType genericSuperclass = (ParameterizedType) this.getClass().getGenericSuperclass();
        this.dataClass = (Class<V>) genericSuperclass.getActualTypeArguments()[2];
        table = dynamoDbEnhancedAsyncClient.table(tableName, TableSchema.fromBean(dataClass));
        tableByIndex = index.length > 0 ? table.index(index[0]) : null;
    }

    public Mono<E> save(E model) {
        return Mono.fromFuture(table.putItem(toData(model))).thenReturn(model);
    }

    public Mono<E> delete(E model) {
        return Mono.fromFuture(table.deleteItem(toData(model))).map(this::toDomain);
    }

    public Mono<E> getById(K id) {
        return Mono.fromFuture(table.getItem(r -> r.key(k -> k.partitionValue(String.valueOf(id)))))
                .flatMap(v -> Objects.isNull(v) ? Mono.empty() : Mono.just(toDomain(v)));
    }

    public Mono<List<E>> query(QueryEnhancedRequest queryExpression) {
        PagePublisher<V> pagePublisher = table.query(queryExpression);
        return listOfModel(pagePublisher);
    }

    public Flux<E> queryFluxByIndex(QueryEnhancedRequest queryExpression, String... index) {
        DynamoDbAsyncIndex<V> queryIndex = index.length > 0 ? table.index(index[0]) : tableByIndex;
        return Flux.from(queryIndex.query(queryExpression))
                .flatMap(page -> Flux.fromIterable(page.items()))
                .map(this::toDomain);
    }

    public Mono<List<E>> queryByIndex(QueryEnhancedRequest queryExpression, String... index) {
        DynamoDbAsyncIndex<V> queryIndex = index.length > 0 ? table.index(index[0]) : tableByIndex;
        SdkPublisher<Page<V>> pagePublisher = queryIndex.query(queryExpression);
        return listOfModel(pagePublisher);
    }

    @Deprecated(forRemoval = true)
    public Flux<E> scanFlux() {
        return Flux.from(table.scan())
                .flatMap(page -> Flux.fromIterable(page.items()))
                .map(this::toDomain);
    }

    private Mono<List<E>> listOfModel(PagePublisher<V> pagePublisher) {
        return Mono.from(pagePublisher).map(page -> page.items().stream().map(this::toDomain).toList());
    }

    private Mono<List<E>> listOfModel(SdkPublisher<Page<V>> pagePublisher) {
        return Mono.from(pagePublisher).map(page -> page.items().stream().map(this::toDomain).toList());
    }

    protected V toData(E model) {
        return toDataFn.apply(model);
    }

    protected E toDomain(V data) {
        return toDomainFn.apply(data);
    }
}
