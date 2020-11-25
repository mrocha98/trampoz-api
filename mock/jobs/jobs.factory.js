const faker = require('faker')
const { formatToSQLPattern } = require('../utils/date')

// '7bfea6d9-fae9-4b22-a518-1111667f81c3'

class JobsFactory {
  constructor () {
    this.cities = ['Sao Paulo', 'Sao Jose dos Campos', 'Campinas', 'Jacarei']
  }

  create (contractorId) {
    return {
      contractorId,
      title: faker.name.title(),
      description: faker.lorem.text(),
      publishingDate: formatToSQLPattern(faker.date.past()),
      state: 'SP',
      city: faker.random.arrayElement(this.cities),
      isRemote: faker.random.boolean(),
      isOpen: faker.random.boolean()
    }
  }
}

module.exports = { JobsFactory }
