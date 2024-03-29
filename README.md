## Currency Converter ![](https://github.com/KwabenBerko/Currency-Converter/actions/workflows/master.yml/badge.svg)

An Offline-First Currency Converter Built With Kotlin Multiplatform. The main aim of this project was to help improve my knowledge in Kotlin Multiplatform(A framework for sharing code across both IOS and Android), ATDD(Acceptance Test Driven Development) and Clean Architecture.


| Android                                        | iOS                                        |
|------------------------------------------------|--------------------------------------------|
| <img src="art/android_demo.gif" height="600"/> | <img src="art/ios_demo.gif" height="600"/> |

The UI design for this project was inspired by a design created
by [Oleg Frolov](https://dribbble.com/Volorf). You can find the original
design [here](https://dribbble.com/shots/3667983-Currency-converter).


## Acceptance Criteria

#### Scenario: Sync currencies

Given the following currencies

| Currencies           |
|----------------------|
| United States Dollar |
| Ghanaian Cedi        |
| Nigerian Naira       |

And the following currency rates

| baseCurrency         | targetCurrency | rate       |
|----------------------|----------------|------------|
| United States Dollar | Ghanaian Cedi  | 10.015024  |
| United States Dollar | Nigerian Naira | 422.990183 |

When a user syncs currencies  
Then the sync should be successful  
And the user should see all currencies  
And the user should see `<rate>` as the rate for `<baseCurrency>` to `<targetCurrency>`

Examples:

| baseCurrency         | targetCurrency       | rate       |
|----------------------|----------------------|------------|
| United States Dollar | Ghanaian Cedi        | 10.015024  | 
| Ghanaian Cedi        | United States Dollar | 0.09985    |
| United States Dollar | Nigerian Naira       | 422.990183 |
| Nigerian Naira       | United States Dollar | 0.002364   | 
| Ghanaian Cedi        | Nigerian Naira       | 42.235564  | 
| Nigerian Naira       | Ghanaian Cedi        | 0.023677   |

#### Scenario: Convert amounts from base to target currencies

Given the following currency rates

| baseCurrency         | targetCurrency       | rate      |
|----------------------|----------------------|-----------|
| United States Dollar | Ghanaian Cedi        | 10.015024 |
| Ghanaian Cedi        | Nigerian Naira       | 42.235564 |
| Nigerian Naira       | British Pound        | 0.002041  |
| Euro                 | United States Dollar | 1.007097  |

When a user converts from `<baseCurrency>` to `<targetCurrency>` with an amount of `<amount>`  
Then the user should have a converted amount of  `<convertedAmount>`

##### Examples:

| baseCurrency         | targetCurrency       | amount | convertedAmount |
|----------------------|----------------------|--------|-----------------|
| United States Dollar | Ghanaian Cedi        | 50     | 500.75          |
| Ghanaian Cedi        | Nigerian Naira       | 2000   | 84471.13        |
| Nigerian Naira       | British Pound        | 1      | 0.0020          |
| Euro                 | United States Dollar | 900    | 906.39          |

#### Scenario: Get currencies available to the user in a sorted order

Given the following currencies

| Currencies           |
|----------------------|
| United States Dollar |
| Ghanaian Cedi        |
| Nigerian Naira       |

When a user retrieves all currencies  
Then the user should see all currencies in a sorted order

#### Scenario: Search currencies

Given the following currencies

| Currencies           |
|----------------------|
| United States Dollar |
| Ghanaian Cedi        |
| Nigerian Naira       |

When a user retrieves currencies with a name filter of `<filter>`   
Then the user should see `<filteredCurrencies>` in a sorted order

Examples:

| filter | filteredCurrencies                                  |
|--------|-----------------------------------------------------|
| e      | Ghanaian Cedi, Nigerian Naira, United States Dollar |
| g      | Ghanaian Cedi, Nigerian Naira                       |
| GHS    | Ghanaian Cedi                                       |
| Ghana  | Ghanaian Cedi                                       |
| naira  | Nigerian Naira                                      |
| $      | United States Dollar                                |

#### Scenario: Get GHS and USD as default base and target currencies respectively

Given the following currencies

| Currencies           |
|----------------------|
| United States Dollar |
| Ghanaian Cedi        |
| Nigerian Naira       |

When a user gets default currencies  
Then the user should have a default base currency of United States Dollar and a default target
currency of Ghanaian Cedi

#### Scenario: Keep track of default base and target currencies during conversions

Given the following currencies

| Currencies           |
|----------------------|
| United States Dollar |
| Ghanaian Cedi        |
| Nigerian Naira       |
| British Pound        |
| Euro                 |

And a user has converted an amount from `<baseCurrency>` to `<targetCurrency>`  
When the user get default currencies  
Then the user should have a default base currency of `<baseCurrency>` and a default target currency
of `<targetCurrency>`

Examples:

| baseCurrency         | targetCurrency       |
|----------------------|----------------------|
| United States Dollar | Ghanaian Cedi        |
| Ghanaian Cedi        | Nigerian Naira       |
| Nigerian Naira       | British Pound        |
| Euro                 | United States Dollar |
