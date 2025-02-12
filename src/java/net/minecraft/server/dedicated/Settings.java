package net.minecraft.server.dedicated;

import com.google.common.base.MoreObjects;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Properties;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import javax.annotation.Nullable;
import net.minecraft.core.RegistryAccess;
import org.slf4j.Logger;

public abstract class Settings<T extends Settings<T>> {
    private static final Logger LOGGER = LogUtils.getLogger();
    protected final Properties properties;

    public Settings(Properties pProperties) {
        this.properties = pProperties;
    }

    public static Properties loadFromFile(Path pPath) {
        try {
            try {
                Properties properties3;
                try (InputStream inputstream = Files.newInputStream(pPath)) {
                    CharsetDecoder charsetdecoder = StandardCharsets.UTF_8
                        .newDecoder()
                        .onMalformedInput(CodingErrorAction.REPORT)
                        .onUnmappableCharacter(CodingErrorAction.REPORT);
                    Properties properties2 = new Properties();
                    properties2.load(new InputStreamReader(inputstream, charsetdecoder));
                    properties3 = properties2;
                }

                return properties3;
            } catch (CharacterCodingException charactercodingexception) {
                LOGGER.info("Failed to load properties as UTF-8 from file {}, trying ISO_8859_1", pPath);

                Properties properties1;
                try (Reader reader = Files.newBufferedReader(pPath, StandardCharsets.ISO_8859_1)) {
                    Properties properties = new Properties();
                    properties.load(reader);
                    properties1 = properties;
                }

                return properties1;
            }
        } catch (IOException ioexception) {
            LOGGER.error("Failed to load properties from file: {}", pPath, ioexception);
            return new Properties();
        }
    }

    public void store(Path pPath) {
        try (Writer writer = Files.newBufferedWriter(pPath, StandardCharsets.UTF_8)) {
            this.properties.store(writer, "Minecraft server properties");
        } catch (IOException ioexception) {
            LOGGER.error("Failed to store properties to file: {}", pPath);
        }
    }

    private static <V extends Number> Function<String, V> wrapNumberDeserializer(Function<String, V> pParseFunc) {
        return p_139845_ -> {
            try {
                return pParseFunc.apply(p_139845_);
            } catch (NumberFormatException numberformatexception) {
                return null;
            }
        };
    }

    protected static <V> Function<String, V> dispatchNumberOrString(IntFunction<V> pById, Function<String, V> pByName) {
        return p_139856_ -> {
            try {
                return pById.apply(Integer.parseInt(p_139856_));
            } catch (NumberFormatException numberformatexception) {
                return pByName.apply(p_139856_);
            }
        };
    }

    @Nullable
    private String getStringRaw(String pKey) {
        return (String)this.properties.get(pKey);
    }

    @Nullable
    protected <V> V getLegacy(String pKey, Function<String, V> pSerializer) {
        String s = this.getStringRaw(pKey);
        if (s == null) {
            return null;
        } else {
            this.properties.remove(pKey);
            return pSerializer.apply(s);
        }
    }

    protected <V> V get(String pKey, Function<String, V> pSerializer, Function<V, String> pDeserializer, V pDefaultValue) {
        String s = this.getStringRaw(pKey);
        V v = MoreObjects.firstNonNull(s != null ? pSerializer.apply(s) : null, pDefaultValue);
        this.properties.put(pKey, pDeserializer.apply(v));
        return v;
    }

    protected <V> Settings<T>.MutableValue<V> getMutable(String pKey, Function<String, V> pSerializer, Function<V, String> pDeserializer, V pDefaultValue) {
        String s = this.getStringRaw(pKey);
        V v = MoreObjects.firstNonNull(s != null ? pSerializer.apply(s) : null, pDefaultValue);
        this.properties.put(pKey, pDeserializer.apply(v));
        return new MutableValue<>(pKey, v, pDeserializer);
    }

    protected <V> V get(String pKey, Function<String, V> pSerializer, UnaryOperator<V> pModifier, Function<V, String> pDeserializer, V pDefaultValue) {
        return this.get(pKey, p_139849_ -> {
            V v = pSerializer.apply(p_139849_);
            return v != null ? pModifier.apply(v) : null;
        }, pDeserializer, pDefaultValue);
    }

    protected <V> V get(String pKey, Function<String, V> pMapper, V pValue) {
        return this.get(pKey, pMapper, Objects::toString, pValue);
    }

    protected <V> Settings<T>.MutableValue<V> getMutable(String pKey, Function<String, V> pSerializer, V pDefaultValue) {
        return this.getMutable(pKey, pSerializer, Objects::toString, pDefaultValue);
    }

    protected String get(String pKey, String pDefaultValue) {
        return this.get(pKey, Function.identity(), Function.identity(), pDefaultValue);
    }

    @Nullable
    protected String getLegacyString(String pKey) {
        return this.getLegacy(pKey, Function.identity());
    }

    protected int get(String pKey, int pDefaultValue) {
        return this.get(pKey, wrapNumberDeserializer(Integer::parseInt), pDefaultValue);
    }

    protected Settings<T>.MutableValue<Integer> getMutable(String pKey, int pDefaultValue) {
        return this.getMutable(pKey, wrapNumberDeserializer(Integer::parseInt), pDefaultValue);
    }

    protected int get(String pKey, UnaryOperator<Integer> pModifier, int pDefaultValue) {
        return this.get(pKey, wrapNumberDeserializer(Integer::parseInt), pModifier, Objects::toString, pDefaultValue);
    }

    protected long get(String pKey, long pDefaultValue) {
        return this.get(pKey, wrapNumberDeserializer(Long::parseLong), pDefaultValue);
    }

    protected boolean get(String pKey, boolean pDefaultValue) {
        return this.get(pKey, Boolean::valueOf, pDefaultValue);
    }

    protected Settings<T>.MutableValue<Boolean> getMutable(String pKey, boolean pDefaultValue) {
        return this.getMutable(pKey, Boolean::valueOf, pDefaultValue);
    }

    @Nullable
    protected Boolean getLegacyBoolean(String pKey) {
        return this.getLegacy(pKey, Boolean::valueOf);
    }

    protected Properties cloneProperties() {
        Properties properties = new Properties();
        properties.putAll(this.properties);
        return properties;
    }

    protected abstract T reload(RegistryAccess pRegistryAccess, Properties pProperties);

    public class MutableValue<V> implements Supplier<V> {
        private final String key;
        private final V value;
        private final Function<V, String> serializer;

        MutableValue(final String pKey, final V pValue, final Function<V, String> pSerializer) {
            this.key = pKey;
            this.value = pValue;
            this.serializer = pSerializer;
        }

        @Override
        public V get() {
            return this.value;
        }

        public T update(RegistryAccess pRegistryAccess, V pNewValue) {
            Properties properties = Settings.this.cloneProperties();
            properties.put(this.key, this.serializer.apply(pNewValue));
            return Settings.this.reload(pRegistryAccess, properties);
        }
    }
}