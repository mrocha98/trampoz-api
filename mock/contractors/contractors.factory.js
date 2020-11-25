const faker = require('faker')
const { formatToSQLPattern } = require('../utils/date')

class ContractorsFactory {
  create () {
    return {
      name: faker.name.findName(),
      email: faker.internet.email(),
      password: faker.internet.password(),
      birthday: formatToSQLPattern(faker.date.past()),
      gender: faker.random.arrayElement(['M', 'F']),
      cnpj: faker.random.uuid().slice(0, 14),
      companyName: faker.company.companyName(),
      companyLogoLink: faker.image.business()
    }
  }
}

module.exports = { ContractorsFactory }
