package Tests;

import org.junit.runners.Suite;
import org.junit.runner.RunWith;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        WebCrawlerTest.class
})

/**
 * Test Suite must be run with the following server on the local host:
 *
 * http://localhost:3001/A
 *
 * The hosted website is arranged as follows:
 *                       ┌─────┐
 *        ┌─────────────▶│  A  │─────────────┐
 *        │              └─────┘             │
 *        │                 │                │
 *        │                 │                │
 *        ▼                 ▼                ▼
 *     ┌─────┐           ┌─────┐          ┌─────┐
 *     │  B  │           │  C  │          │  D  │◀─────┐
 *     └─────┘           └─────┘          └─────┘      │
 *        │                 │                │         │
 *    ┌───┴──┐          ┌───┴──┐          ┌──┴───┐     │
 *    ▼      ▼          ▼      ▼          ▼      ▼     │
 * ┌─────┐┌─────┐    ┌─────┐┌─────┐    ┌─────┐┌─────┐  │
 * │  E  ││  F  │◀───│  G  ││  H  │    │  I  ││  J  │  │
 * └─────┘└─────┘    └─────┘└─────┘    └─────┘└─────┘  │
 *    │                        ▲          ▲      │     │
 *    │                        │          │      │     │
 *    ▼                        │          ▼      ▼     │
 * ┌─────┐                     │       ┌─────┐┌─────┐  │
 * │  K  │─────────────────────┼──────▶│  L  ││  M  │──┘
 * └─────┘                     │       └─────┘└─────┘
 *                             │                 │
 *                             │                 │
 *                             │                 ▼
 *                             │              ┌─────┐
 *                             └──────────────│  N  │
 *                                            └─────┘
 */

public class TestSuite {}
