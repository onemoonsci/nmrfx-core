module org.nmrfx.core {
    exports org.nmrfx.datasets;
    exports org.nmrfx.peaks;
    exports org.nmrfx.chemistry;
    exports org.nmrfx.chemistry.io;
    exports org.nmrfx.chemistry.utilities;
    exports org.nmrfx.chemistry.constraints;
    exports org.nmrfx.chemistry.protein;
    exports org.nmrfx.chemistry.search;
    exports org.nmrfx.math;
    exports org.nmrfx.math.units;
    exports org.nmrfx.peaks.io;
    exports org.nmrfx.star;
    exports org.nmrfx.project;
    exports org.nmrfx.server;
    exports org.nmrfx.utilities;
    requires commons.math3;
    requires org.apache.commons.lang3;
    requires org.apache.commons.collections4;
    requires io.netty.all;
    requires java.logging;
    requires java.desktop;
    requires vecmath;
    requires com.google.common;
    requires jython.slim;
}