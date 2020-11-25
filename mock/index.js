const faker = require('faker')
const consola = require('consola')
const { get, isEmpty, omit } = require('lodash')
const { keyInSelect } = require('readline-sync')
const { ContractorsFactory, ContractorsService } = require('./contractors')
const { JobsFactory, JobsService } = require('./jobs')

async function createContractor () {
  try {
    const contractor = ContractorsFactory.create()
    const { data } = await ContractorsService.create(contractor)
    consola.success({ contractor: data })
  } catch (error) {
    consola.error(get(error, 'message'))
  }
}

async function createJob () {
  try {
    const contractors = await ContractorsService.getIds()

    const NO_CONTRACTORS = isEmpty(contractors)
    if (NO_CONTRACTORS) {
      consola.warn('No contractors were found! At least one is required to create a new job.')
      return
    }

    const contractorId = faker.random.arrayElement(contractors)
    const job = JobsFactory.create(contractorId)

    const { data } = await JobsService.create(job)
    const toLog = omit(data, ['contractorId'])
    consola.success(toLog)
  } catch (error) {
    consola.error(get(error, 'message'))
  }
}

;(async () => {
  while (true) {
    const selectOptions = ['CONTRACTOR', 'JOB']
    const selection = keyInSelect(selectOptions, 'Which one do you want to create?')

    const wasContractorSelected = selection === 0
    const wasJobSelected = selection === 1

    if (wasContractorSelected) await createContractor()
    else if (wasJobSelected) await createJob()
    else {
      consola.info('Bye!')
      break
    }
  }
})()
