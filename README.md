## Currency Converter ![](https://github.com/KwabenBerko/Currency-Converter/actions/workflows/master.yml/badge.svg)

An Offline-First Currency Converter Built With Kotlin Multiplatform Mobile

## Acceptance Criteria

#### Scenario: Sync currencies

Given the following currencies

| Currencies |
|------------|
| USD        |
| GHS        |
| NGN        |

And the following currency rates

| baseCurrency | targetCurrency | rate   |
|--------------|----------------|--------|
| USD          | GHS            | 9.08   |
| USD          | NGN            | 419.22 |

When a user syncs currencies  
Then the sync should be successful  
And the user should see all currencies  
And the user should see `<rate>` as the rate for `<baseCurrency>` to `<targetCurrency>`

Examples:
| baseCurrency | targetCurrency | rate | |--------------|----------------|--------| | USD | GHS |
9.08 | | GHS | USD | 0.11 | | USD | NGN | 419.22 | | NGN | USD | 0.0024 | | GHS | NGN | 46.11 | |
NGN | GHS | 0.022 |

#### Scenario: Convert amounts from base to target currencies

Given the following currency rates

| baseCurrency | targetCurrency | rate      |
|--------------|----------------|-----------|
| USD          | GHS            | 7.775     |
| GHS          | NGN            | 53.347445 |
| NGN          | GBP            | 0.0019289 |
| EUR          | USD            | 1.07823   |

When a user converts from `<baseCurrency>` to `<targetCurrency>` with an amount of `<amount>`  
Then the user should have a converted amount of  `<convertedAmount>`

##### Examples:

| baseCurrency | targetCurrency | amount | convertedAmount |
|--------------|----------------|--------|-----------------|
| USD          | GHS            | 50     | 388.75          |
| GHS          | NGN            | 2000   | 106694.89       |
| NGN          | GBP            | 100000 | 192.89          |
| EUR          | USD            | 900    | 970.41          |

#### Scenario: Get currencies available to the user in a sorted order

Given the following currencies

| Currencies |
|------------|
| USD        |
| GHS        |
| NGN        |

When a user retrieves all currencies  
Then the user should see all currencies in a sorted order

#### Scenario: Get GHS and USD as default base and target currencies respectively

Given the following currencies

| Currencies |
|------------|
| USD        |
| GHS        |
| NGN        |

When a user gets default currencies  
Then the user should have a default base currency of USD and a default target currency of GHS

#### Scenario: Keep track of default base and target currencies during conversions

Given the following currencies

| Currencies |
|------------|
| USD        |
| GHS        |
| NGN        |
| GBP        |
| EUR        |

And the following currency rates

| baseCurrency | targetCurrency | rate      |
|--------------|----------------|-----------|
| USD          | GHS            | 7.775     |
| GHS          | NGN            | 53.347445 |
| NGN          | GBP            | 0.0019289 |
| EUR          | USD            | 1.07823   |

And a user has converted an amount from `<baseCurrency>` to `<targetCurrency>`  
When the user get default currencies  
Then the user should have a default base currency of `<baseCurrency>` and a default target currency
of `<targetCurrency>`

Examples:

| baseCurrency | targetCurrency |
|--------------|----------------|
| USD          | GHS            |
| GHS          | NGN            |
| NGN          | GBP            |
| EUR          | USD            |
