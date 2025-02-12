package net.minecraft.client.telemetry.events;

import com.google.common.base.Stopwatch;
import com.google.common.base.Ticker;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import java.util.HashMap;
import java.util.Map;
import java.util.OptionalLong;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import net.minecraft.client.telemetry.TelemetryEventSender;
import net.minecraft.client.telemetry.TelemetryEventType;
import net.minecraft.client.telemetry.TelemetryProperty;
import net.minecraft.client.telemetry.TelemetryPropertyMap;
import org.slf4j.Logger;

public class GameLoadTimesEvent {
    public static final GameLoadTimesEvent INSTANCE = new GameLoadTimesEvent(Ticker.systemTicker());
    private static final Logger LOGGER = LogUtils.getLogger();
    private final Ticker timeSource;
    private final Map<TelemetryProperty<GameLoadTimesEvent.Measurement>, Stopwatch> measurements = new HashMap<>();
    private OptionalLong bootstrapTime = OptionalLong.empty();

    protected GameLoadTimesEvent(Ticker pTimeSource) {
        this.timeSource = pTimeSource;
    }

    public synchronized void beginStep(TelemetryProperty<GameLoadTimesEvent.Measurement> pMeasurement) {
        this.beginStep(pMeasurement, p_286494_ -> Stopwatch.createStarted(this.timeSource));
    }

    public synchronized void beginStep(TelemetryProperty<GameLoadTimesEvent.Measurement> pMeasurement, Stopwatch pStopwatch) {
        this.beginStep(pMeasurement, p_286421_ -> pStopwatch);
    }

    private synchronized void beginStep(
        TelemetryProperty<GameLoadTimesEvent.Measurement> pMeasurement, Function<TelemetryProperty<GameLoadTimesEvent.Measurement>, Stopwatch> pStopwatchGetter
    ) {
        this.measurements.computeIfAbsent(pMeasurement, pStopwatchGetter);
    }

    public synchronized void endStep(TelemetryProperty<GameLoadTimesEvent.Measurement> pMeasurement) {
        Stopwatch stopwatch = this.measurements.get(pMeasurement);
        if (stopwatch == null) {
            LOGGER.warn("Attempted to end step for {} before starting it", pMeasurement.id());
        } else {
            if (stopwatch.isRunning()) {
                stopwatch.stop();
            }
        }
    }

    public void send(TelemetryEventSender pSender) {
        pSender.send(
            TelemetryEventType.GAME_LOAD_TIMES,
            p_286285_ -> {
                synchronized (this) {
                    this.measurements
                        .forEach(
                            (p_286804_, p_286275_) -> {
                                if (!p_286275_.isRunning()) {
                                    long i = p_286275_.elapsed(TimeUnit.MILLISECONDS);
                                    p_286285_.put(
                                        (TelemetryProperty<GameLoadTimesEvent.Measurement>)p_286804_, new GameLoadTimesEvent.Measurement((int)i)
                                    );
                                } else {
                                    LOGGER.warn(
                                        "Measurement {} was discarded since it was still ongoing when the event {} was sent.",
                                        p_286804_.id(),
                                        TelemetryEventType.GAME_LOAD_TIMES.id()
                                    );
                                }
                            }
                        );
                    this.bootstrapTime.ifPresent(p_286872_ -> p_286285_.put(TelemetryProperty.LOAD_TIME_BOOTSTRAP_MS, new GameLoadTimesEvent.Measurement((int)p_286872_)));
                    this.measurements.clear();
                }
            }
        );
    }

    public synchronized void setBootstrapTime(long pBootstrapTime) {
        this.bootstrapTime = OptionalLong.of(pBootstrapTime);
    }

    public static record Measurement(int millis) {
        public static final Codec<GameLoadTimesEvent.Measurement> CODEC = Codec.INT
            .xmap(GameLoadTimesEvent.Measurement::new, p_286736_ -> p_286736_.millis);
    }
}