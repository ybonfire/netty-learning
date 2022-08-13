package org.ybonfire.pipeline.nameserver.route.provider;

import org.ybonfire.pipeline.nameserver.route.RouteManageService;
import org.ybonfire.pipeline.nameserver.route.impl.InMemoryRouteRepository;

/**
 * 这里添加类的注释【强制】
 *
 * @author Bo.Yuan5
 * @date 2022-08-05 18:21
 */
public class RouteManageServiceProvider {
    private static final RouteManageService INSTANCE = new RouteManageService(new InMemoryRouteRepository());
}
