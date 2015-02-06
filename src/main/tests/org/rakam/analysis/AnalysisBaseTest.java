package org.rakam.analysis;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Scopes;
import org.junit.Before;
import org.junit.BeforeClass;
import org.rakam.RakamTestHelper;
import org.rakam.database.EventDatabase;
import org.rakam.database.ReportDatabase;
import org.rakam.stream.ActorCacheAdapter;
import org.rakam.stream.local.LocalCache;
import org.rakam.stream.local.LocalCacheImpl;

import java.io.FileNotFoundException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * Created by buremba <Burak Emre Kabakcı> on 01/11/14 00:49.
 */
public class AnalysisBaseTest extends RakamTestHelper {
    static DummyDatabase analysisRuleMap;

    protected static String formatTime(int time) {
        return Instant.ofEpochSecond(time).atZone(ZoneId.of("UTC")).format(DateTimeFormatter.ISO_INSTANT);
    }

    @BeforeClass
    public static void start() throws FileNotFoundException {
        Injector injector = Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                bind(LocalCache.class).to(LocalCacheImpl.class);
                bind(LocalCacheImpl.class).in(Scopes.SINGLETON);
                bind(EventDatabase.class).to(DummyDatabase.class);
                bind(ReportDatabase.class).to(DummyDatabase.class);
                bind(ActorCacheAdapter.class).to(DummyDatabase.class);
            }
        });

        analysisRuleMap = new DummyDatabase();

    }

    @Before
    public void clear() {
        analysisRuleMap.clear();

    }

}