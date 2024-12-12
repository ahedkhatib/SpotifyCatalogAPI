package com.example.catalog;

import com.example.catalog.utils.LRUCache;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LRUCacheTest {

    LRUCache<String, String> cache;

    @Nested
    @DisplayName("when instantiated with capacity 3")
    class WhenInstantiated {

        @BeforeEach
        void createNewCache() {
            cache = new LRUCache<>(3);
        }

        @Test
        @DisplayName("cache is initially empty")
        void isEmpty() {
            // TODO assert cache is empty
            assertTrue(cache.isEmpty());
        }

        @Test
        @DisplayName("throws NullPointerException when getting a null key")
        void throwsExceptionWhenGettingNullKey() {
            // TODO assert NullPointerException thrown on `cache.get(null)`
            assertThrows(NullPointerException.class, () -> cache.get(null));
        }

        @Test
        @DisplayName("throws NullPointerException when setting a null key")
        void throwsExceptionWhenSettingNullKey() {
            assertThrows(NullPointerException.class, () -> cache.set(null, ""));
        }

        @Nested
        @DisplayName("after adding 2 elements")
        class AfterAdding2Elements {

            @BeforeEach
            void addElements() {
                // TODO add 2 elements
                cache.set("k1", "v1");
                cache.set("k2", "v2");
            }

            @Test
            @DisplayName("cache contains the added elements")
            void containsAddedElements() {
                // TODO assert the added 2 elements are available
                assertEquals("v1", cache.get("k1"));
                assertNull(cache.get("k4"));
            }

            @Test
            @DisplayName("cache maintains the same size and content after adding duplicate keys")
            void maintainsSameSizeAfterAdd(){
                cache.set("k1", "v11");
                assertEquals(2,cache.size());
                assertEquals("v11", cache.get("k1"));
            }

            @Test
            @DisplayName("ablity to add one more element without revoke another existed keys")
            void addingElementsWithoutRevoking(){
                cache.set("k3", "v3");
                assertEquals("v3", cache.get("k3"));
                assertEquals("v2", cache.get("k2"));
                assertEquals("v1", cache.get("k1"));
                assertEquals(3,cache.size());
            }

            @Test
            @DisplayName("maintains correct order of elements after accessing an existing element")
            void maintainCorrectOrderAfterAccess(){
                cache.get("k1");
                assertEquals("v2", cache.get("k2"));
                assertEquals("v1", cache.get("k1"));
                assertNull(cache.get("k3"));
            }

            @Test
            @DisplayName("updates the value of an existing key when set again")
            void updateValue(){
                cache.set("k1", "v11");
                assertEquals("v11", cache.get("k1"));
                assertEquals("v2", cache.get("k2"));
                assertEquals(2, cache.size());
            }
        }

        @Nested
        @DisplayName("after adding 3 elements")
        class AfterAdding3Elements {

            @BeforeEach
            void addElements() {
                // TODO add 3 elements
                cache.set("k1", "v1");
                cache.set("k2", "v2");
                cache.set("k3", "v3");
            }

            @Test
            @DisplayName("size does not exceed capacity after adding more elements")
            void doesNotExceedCapacity(){
                cache.set("k4", "v4");
                assertEquals(3, cache.size());
                assertEquals("v2", cache.get("k2"));
                assertEquals("v3", cache.get("k3"));
                assertEquals("v4", cache.get("k4"));
            }

            @Test
            @DisplayName("evicts the least recently used element when the cache exceeds its capacity")
            void evictsLeastUsed(){
                cache.set("k4", "v4");
                assertNull(cache.get("k1"));
                assertEquals(3, cache.size());
                assertEquals("v2", cache.get("k2"));
                assertEquals("v3", cache.get("k3"));
                assertEquals("v4", cache.get("k4"));
            }

            @Nested
            @DisplayName("when cleared")
            class WhenCleared {

                // addElements (in AfterAdding3Elements) is executed and then clearCache
                // before EACH test case in WhenCleared


                @BeforeEach
                void clearCache() {
                    // TODO clear the cache after
                    cache.clear();
                }

                @Test
                @DisplayName("cache is empty")
                void isEmpty(){
                    assertTrue(cache.isEmpty());
                }

                @Test
                @DisplayName("added elements are not accesible")
                void notAccessibleAfterClear(){
                    assertNull(cache.get("k1"));
                    assertNull(cache.get("k2"));
                    assertNull(cache.get("k3"));
                    assertEquals(0, cache.size());
                }
            }
        }

        @Nested
        @DisplayName("Boundary conditions")
        class BoundaryConditions {

            @Test
            @DisplayName("adding elements up to exactly the capacity without eviction")
            void handlesExactlyCapacity(){
                cache.set("k1", "v1");
                cache.set("k2", "v2");
                cache.set("k3", "v3");
                assertEquals("v1", cache.get("k1"));
                assertEquals("v2", cache.get("k2"));
                assertEquals("v3", cache.get("k3"));
                assertEquals(3, cache.size());
            }

            @Test
            @DisplayName("evicts only the least recently used element when capacity is exceeded repeatedly")
            void evictsLeastRecentlyElement(){
                cache.set("k1", "v1");
                cache.set("k2", "v2");
                cache.set("k3", "v3");
                cache.set("k4", "v4");
                assertNull(cache.get("k1"));
                cache.set("k5", "v5");
                assertNull(cache.get("k2"));
                assertNotNull(cache.get("k3"));
                assertNotNull(cache.get("k4"));
                assertNotNull(cache.get("k5"));
            }

            @Test
            @DisplayName("Retrieve non existing key")
            void nonExistKey(){
                cache.set("k1", "v1");
                cache.set("k2", "v2");
                assertNull(cache.get("k3"));
            }

            @Test
            @DisplayName("Clear operation is correct")
            void clearOperation(){
                cache.set("k1", "v1");
                cache.set("k2", "v2");
                cache.set("k3", "v3");
                cache.clear();
                assertNull(cache.get("k1"));
                assertNull(cache.get("k2"));
                assertNull(cache.get("k3"));
                assertEquals(0, cache.size());
            }

        }
    }
}
