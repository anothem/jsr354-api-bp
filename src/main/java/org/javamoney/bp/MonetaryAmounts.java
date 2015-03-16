/*
 * CREDIT SUISSE IS WILLING TO LICENSE THIS SPECIFICATION TO YOU ONLY UPON THE CONDITION THAT YOU
 * ACCEPT ALL OF THE TERMS CONTAINED IN THIS AGREEMENT. PLEASE READ THE TERMS AND CONDITIONS OF THIS
 * AGREEMENT CAREFULLY. BY DOWNLOADING THIS SPECIFICATION, YOU ACCEPT THE TERMS AND CONDITIONS OF
 * THE AGREEMENT. IF YOU ARE NOT WILLING TO BE BOUND BY IT, SELECT THE "DECLINE" BUTTON AT THE
 * BOTTOM OF THIS PAGE. Specification: JSR-354 Money and Currency API ("Specification") Copyright
 * (c) 2012-2013, Credit Suisse All rights reserved.
 */
package org.javamoney.bp;

import org.javamoney.bp.spi.Bootstrap;
import org.javamoney.bp.spi.MonetaryAmountsSingletonQuerySpi;
import org.javamoney.bp.spi.MonetaryAmountsSingletonSpi;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Singleton class for accessing {@link MonetaryAmount} instances as provided by the different registered
 * {@link MonetaryAmountFactory} instances.
 * <p>
 * This singleton allows to access {@link MonetaryAmountFactory} instances for the registered
 * {@link MonetaryAmount} implementation classes or using a flexible {@link MonetaryAmountFactoryQuery}
 * instance, determining the selection attributes arbitrarely.
 *
 * @author Anatole Tresch
 * @author Werner Keil
 * @version 0.6.1
 */
public final class MonetaryAmounts {
    /**
     * The used {@link org.javamoney.bp.spi.MonetaryAmountsSingletonSpi} instance.
     */
    private static final MonetaryAmountsSingletonSpi monetaryAmountsSingletonSpi = loadMonetaryAmountsSingletonSpi();

    /**
     * The used {@link org.javamoney.bp.spi.MonetaryAmountsSingletonSpi} instance.
     */
    private static final MonetaryAmountsSingletonQuerySpi monetaryAmountsSingletonQuerySpi =
            loadMonetaryAmountsSingletonQuerySpi();

    /**
     * Private singleton constructor.
     */
    private MonetaryAmounts() {
    }

    /**
     * Loads the SPI backing bean.
     *
     * @return the MonetaryAmountsSingletonSpi bean from the bootstrapping logic.
     */
    private static MonetaryAmountsSingletonSpi loadMonetaryAmountsSingletonSpi() {
        try {
            return Bootstrap.getService(MonetaryAmountsSingletonSpi.class);
        } catch (Exception e) {
            Logger.getLogger(MonetaryCurrencies.class.getName())
                    .log(Level.SEVERE, "Failed to load MonetaryAmountsSingletonSpi.", e);
            return null;
        }
    }

    /**
     * Loads the SPI backing bean.
     *
     * @return the MonetaryAmountsSingletonQuerySpi bean from the bootstrapping logic.
     */
    private static MonetaryAmountsSingletonQuerySpi loadMonetaryAmountsSingletonQuerySpi() {
        try {
            return Bootstrap.getService(MonetaryAmountsSingletonQuerySpi.class);
        } catch (Exception e) {
            Logger.getLogger(MonetaryCurrencies.class.getName()).log(Level.SEVERE, "Failed to load " +
                    "MonetaryAmountsSingletonQuerySpi, " +
                    "query functionality will not be " +
                    "available.", e);
            return null;
        }
    }

    /**
     * Access an {@link MonetaryAmountFactory} for the given {@link MonetaryAmount} implementation
     * type.
     *
     * @param amountType {@link MonetaryAmount} implementation type, nor {@code null}.
     * @return the corresponding {@link MonetaryAmountFactory}, never {@code null}.
     * @throws MonetaryException if no {@link MonetaryAmountFactory} targeting the given {@link MonetaryAmount}
     *                           implementation class is registered.
     */
    public static <T extends MonetaryAmount> MonetaryAmountFactory<T> getAmountFactory(Class<T> amountType) {
        if(monetaryAmountsSingletonQuerySpi==null){
            throw new MonetaryException(
                    "No MonetaryAmountsSingletonQuerySpi loaded, query functionality is not available.");
        }
        MonetaryAmountFactory<T> factory = monetaryAmountsSingletonSpi.getAmountFactory(amountType);
        if(factory==null){
            throw new MonetaryException("No AmountFactory available for type: " + amountType.getName());
        }
        return factory;
    }

    /**
     * Access the default {@link MonetaryAmountFactory} as defined by
     * {@link org.javamoney.bp.spi.MonetaryAmountsSingletonSpi#getDefaultAmountFactory()}.
     *
     * @return the {@link MonetaryAmountFactory} corresponding to default amount type,
     * never {@code null}.
     * @throws MonetaryException if no {@link MonetaryAmountFactory} targeting the default amount type
     *                           implementation class is registered.
     */
    public static MonetaryAmountFactory<?> getDefaultAmountFactory() {
        if(monetaryAmountsSingletonSpi==null){
            throw new MonetaryException(
                    "No MonetaryAmountsSingletonSpi loaded, query functionality is not available.");
        }
        return monetaryAmountsSingletonSpi.getDefaultAmountFactory();
    }

    /**
     * Access all currently available {@link MonetaryAmount} implementation classes that are
     * accessible from this {@link MonetaryAmount} singleton.
     *
     * @return all currently available {@link MonetaryAmount} implementation classes that have
     * corresponding {@link MonetaryAmountFactory} instances provided, never {@code null}
     */
    public static Collection<MonetaryAmountFactory<?>> getAmountFactories() {
        if(monetaryAmountsSingletonSpi==null){
            throw new MonetaryException(
                    "No MonetaryAmountsSingletonSpi loaded, query functionality is not available.");
        }
        return monetaryAmountsSingletonSpi.getAmountFactories();
    }

    /**
     * Access all currently available {@link MonetaryAmount} implementation classes that are
     * accessible from this {@link MonetaryAmount} singleton.
     *
     * @return all currently available {@link MonetaryAmount} implementation classes that have
     * corresponding {@link MonetaryAmountFactory} instances provided, never {@code null}
     */
    public static Collection<Class<? extends MonetaryAmount>> getAmountTypes() {
        if(monetaryAmountsSingletonSpi==null){
            throw new MonetaryException(
                    "No MonetaryAmountsSingletonSpi loaded, query functionality is not available.");
        }
        return monetaryAmountsSingletonSpi.getAmountTypes();
    }

    /**
     * Access the default {@link MonetaryAmount} implementation class that is
     * accessible from this {@link MonetaryAmount} singleton.
     *
     * @return all current default {@link MonetaryAmount} implementation class, never {@code null}
     */
    public static Class<? extends MonetaryAmount> getDefaultAmountType() {
        if(monetaryAmountsSingletonSpi==null){
            throw new MonetaryException(
                    "No MonetaryAmountsSingletonSpi loaded, query functionality is not available.");
        }
        return monetaryAmountsSingletonSpi.getDefaultAmountType();
    }

    /**
     * Executes the query and returns the factory found, if there is only one factory.
     * If multiple factories match the query, one is selected.
     *
     * @param query the factory query, not null.
     * @return the factory found, or null.
     */
    public static MonetaryAmountFactory getAmountFactory(MonetaryAmountFactoryQuery query) {
        if(monetaryAmountsSingletonQuerySpi==null){
            throw new MonetaryException(
                    "No MonetaryAmountsSingletonQuerySpi loaded, query functionality is not available.");
        }
        return monetaryAmountsSingletonQuerySpi.getAmountFactory(query);
    }

    /**
     * Returns all factory instances that match the query.
     *
     * @param query the factory query, not null.
     * @return the instances found, never null.
     */
    public static Collection<MonetaryAmountFactory<?>> getAmountFactories(MonetaryAmountFactoryQuery query) {
        if(monetaryAmountsSingletonQuerySpi==null){
            throw new MonetaryException(
                    "No MonetaryAmountsSingletonQuerySpi loaded, query functionality is not available.");
        }
        return monetaryAmountsSingletonQuerySpi.getAmountFactories(query);
    }

    /**
     * Allows to check if any of the <i>get</i>XXX methods return non empty/non null results of {@link MonetaryAmountFactory}.
     *
     * @param query the factory query, not null.
     * @return true, if at least one {@link MonetaryAmountFactory} matches the query.
     */
    public static boolean isAvailable(MonetaryAmountFactoryQuery query) {
        if(monetaryAmountsSingletonQuerySpi==null){
            throw new MonetaryException(
                    "No MonetaryAmountsSingletonQuerySpi loaded, query functionality is not available.");
        }
        return monetaryAmountsSingletonQuerySpi.isAvailable(query);
    }

}