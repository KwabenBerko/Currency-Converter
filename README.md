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

| baseCurrency | targetCurrency | rate       |
|--------------|----------------|------------|
| USD          | GHS            | 10.015024  |
| USD          | NGN            | 422.990183 |

When a user syncs currencies  
Then the sync should be successful  
And the user should see all currencies  
And the user should see `<rate>` as the rate for `<baseCurrency>` to `<targetCurrency>`

Examples:

| baseCurrency | targetCurrency | rate       |
|--------------|----------------|------------|
| USD          | GHS            | 10.015024  | 
| GHS          | USD            | 0.09985    |
| USD          | NGN            | 422.990183 |
| NGN          | USD            | 0.002364   | 
| GHS          | NGN            | 42.235564  | 
| NGN          | GHS            | 0.023677   |

#### Scenario: Convert amounts from base to target currencies

Given the following currency rates

| baseCurrency | targetCurrency | rate      |
|--------------|----------------|-----------|
| USD          | GHS            | 10.015024 |
| GHS          | NGN            | 42.235564 |
| NGN          | GBP            | 0.002041  |
| EUR          | USD            | 1.007097  |

When a user converts from `<baseCurrency>` to `<targetCurrency>` with an amount of `<amount>`  
Then the user should have a converted amount of  `<convertedAmount>`

##### Examples:

| baseCurrency | targetCurrency | amount | convertedAmount |
|--------------|----------------|--------|-----------------|
| USD          | GHS            | 50     | 500.75          |
| GHS          | NGN            | 2000   | 84471.13        |
| NGN          | GBP            | 100000 | 204.1           |
| EUR          | USD            | 900    | 906.39          |

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
