/* tslint:disable */
/* eslint-disable */
// Generated using typescript-generator version 3.2.1263 on 2026-01-30 11:45:12.

export interface ViewTableLocations extends Serializable {
    locationId: number;
    locationName: string;
    locationNameShort: string;
    locationRegion: string;
    locationState: string;
    locationType: string;
    locationLatitude: number;
    locationLongitude: number;
    locationElevation: number;
    locationCoordinateUncertainty: number;
    countryId: number;
    countryName: string;
    countryCode2: string;
    countryCode3: string;
}

export interface ViewTablePublications extends Serializable {
    publicationId: number;
    publicationDoi: string;
    publicationFallbackCache: string;
    isDatabasePub: number;
    datasetIds: number[];
    germplasmIds: number[];
    groupIds: number[];
    experimentIds: number[];
    projectIds: number[];
    createdOn: Date;
    updatedOn: Date;
}

export interface PaginatedResult<T> extends Serializable {
    data: T;
    count: number;
}

export interface LoginDetails {
    username: string;
    password: string;
}

export interface UnapprovedUsers extends Serializable {
    id: number;
    userUsername: string;
    userPassword: string;
    userFullName: string;
    userEmailAddress: string;
    institutionId: number;
    institutionName: string;
    institutionAcronym: string;
    institutionAddress: string;
    databaseSystemId: number;
    createdOn: Date;
    hasBeenRejected: number;
    needsApproval: number;
    activationKey: string;
}

export interface AlleleFrequencyDatasetRequest extends SubsettedGenotypeDatasetRequest {
    config: BinningConfig;
}

export interface AsyncExportResult {
    status: string;
    uuid: string;
}

export interface BackupResult {
    timestamp: Date;
    germinateVersion: string;
    filename: string;
    type: BackupType;
    filesize: number;
}

export interface ClientAdminConfiguration extends ClientConfiguration {
    bcryptSalt: number;
    brapiEnabled: boolean;
    dataDirectoryExternal: string;
    gatekeeperUsername: string;
    gatekeeperPassword: string;
    gatekeeperRegistrationRequiresApproval: boolean;
    pdciEnabled: boolean;
    filesDeleteAfterHoursAsync: number;
    filesDeleteAfterHoursTemp: number;
    hiddenPagesAutodiscover: boolean;
    databaseBackupEveryDays: number;
    databaseBackupMaxSizeGB: number;
}

export interface ClientConfiguration {
    authMode: AuthenticationMode;
    colorsTemplate: string[];
    colorsCharts: string[];
    colorsGradient: string[];
    colorPrimary: string;
    commentsEnabled: boolean;
    dashboardCategories: string[];
    dashboardSections: string[];
    dataImportMode: DataImportMode;
    externalLinkIdentifier: string;
    externalLinkTemplate: string;
    googleAnalyticsKey: string;
    genesysAvailable: boolean;
    plausibleDomain: string;
    plausibleHashMode: boolean;
    plausibleApiHost: string;
    gatekeeperUrl: string;
    hiddenPages: string[];
    registrationEnabled: boolean;
    showGdprNotification: boolean;
    gridscoreUrl: string;
    heliumUrl: string;
    fieldhubUrl: string;
    hiddenColumns: HiddenColumns;
    supportsFeedback: boolean;
    genesysUrl: string;
}

export interface ClimateDatasetStats {
    datasets: ViewTableDatasets[];
    climates: ViewTableClimates[];
    stats: Quantiles[];
}

export interface DatabaseConfig {
    host: string;
    database: string;
    port: string;
    username: string;
    password: string;
}

export interface DatasetCrossDataTypeRequest {
    first: Config;
    second: Config;
}

export interface DatasetExportRequest extends ExportRequest {
    datasetIds: number[];
}

export interface DatasetGroupModificationRequest {
    datasetId: number;
    groupIds: number[];
    addOperation: boolean;
}

export interface DatasetGroupRequest extends DatasetRequest {
    datasetType: string;
    groupType: string;
}

export interface DatasetRequest {
    datasetIds: number[];
}

export interface DatasetUserModificationRequest {
    datasetId: number;
    userIds: number[];
    addOperation: boolean;
}

export interface EntityTypeStats {
    entityTypeId: number;
    entityTypeName: string;
    count: number;
}

export interface ExperimentRequest extends DatasetRequest {
    experimentId: number;
}

export interface ExportRequest {
    filters: FilterGroup[];
    columnNameMapping: { [index: string]: string };
    forcedFileExtension: string;
}

export interface Filter {
    column: string;
    comparator: FilterComparator;
    values: string[];
    canBeChanged: boolean;
    safeColumn: string;
}

export interface FilterGroup {
    filters: Filter[];
    filterGroups: FilterGroup[];
    operator: FilterOperator;
}

export interface GatekeeperConfig {
    url: string;
    username: string;
    password: string;
}

export interface GenesysRequestDetails {
    name: string;
    email: string;
    germplasmIds: number[];
}

export interface GermplasmDistance extends ViewTableGermplasm {
    distance: number;
}

export interface GermplasmExportRequest extends ExportRequest {
    individualIds: number[];
    groupIds: number[];
    includeAttributes: boolean;
}

export interface GermplasmStats {
    germplasmId: number;
    germplasmName: string;
    traitId: number;
    traitName: string;
    traitNameShort: string;
    min: number;
    avg: number;
    max: number;
    count: number;
}

export interface GermplasmUnificationRequest {
    preferredGermplasmId: number;
    otherGermplasmIds: number[];
    explanation: string;
}

export interface GroupModificationRequest {
    ids: number[];
    addition: boolean;
}

export interface HiddenColumns {
    germplasm: string[];
    germplasmAttributes: string[];
    images: string[];
    climates: string[];
    climateData: string[];
    comments: string[];
    fileresources: string[];
    maps: string[];
    markers: string[];
    mapDefinitions: string[];
    datasets: string[];
    datasetAttributes: string[];
    experiments: string[];
    entities: string[];
    news: string[];
    backups: string[];
    dataUpdate: string[];
    taxonomies: string[];
    groups: string[];
    institutions: string[];
    locations: string[];
    pedigrees: string[];
    projects: string[];
    pedigreedefinitions: string[];
    traits: string[];
    trialsData: string[];
    collaborators: string[];
    publications: string[];
}

export interface ImageTagModificationRequest {
    tags: string[];
    addition: boolean;
}

export interface InstitutionUnificationRequest {
    preferredInstitutionId: number;
    institutionIds: number[];
}

export interface LatLng {
    lat: number;
    lng: number;
}

export interface LinkRequest {
    targetTable: string;
    foreignId: number;
}

export interface LocaleConfig {
    locale: string;
    name: string;
    flag: string;
}

export interface LocationDistance extends ViewTableLocations {
    distance: number;
}

export interface MapExportRequest {
    format: string;
    method: string;
    chromosomes: string[];
    regions: Region[];
    markerIdInterval: number[];
    radius: Radius;
}

export interface NewUnapprovedUserRequest {
    user: NewUnapprovedUser;
    locale: string;
}

export interface NewUserAccessRequest {
    username: string;
    password: string;
    locale: string;
}

export interface OverviewStats {
    germplasm: number;
    markers: number;
    maps: number;
    traits: number;
    climates: number;
    locations: number;
    datasets: number;
    datasetsGenotype: number;
    datasetsTrials: number;
    datasetsAllelefreq: number;
    datasetsClimate: number;
    datasetsPedigree: number;
    experiments: number;
    groups: number;
    images: number;
    fileresources: number;
    publications: number;
    dataStories: number;
    projects: number;
    taxonomies: number;
}

export interface PaginatedDatasetRequest extends PaginatedRequest {
    datasetIds: number[];
}

export interface PaginatedLocationRequest extends PaginatedRequest {
    latitude: number;
    longitude: number;
}

export interface PaginatedPolygonRequest extends PaginatedRequest {
    polygons: LatLng[][];
}

export interface PaginatedRequest {
    orderBy: string;
    ascending: number;
    limit: number;
    page: number;
    prevCount: number;
    minimal: boolean;
    filters: FilterGroup[];
}

export interface PedigreeRequest extends TrialsExportDatasetRequest {
    levelsUp: number;
    levelsDown: number;
    includeAttributes: boolean;
}

export interface ProjectStats {
    publicationCount: number;
    groupCount: number;
    datasetCount: number;
    collaboratorCount: number;
}

export interface Quantiles {
    datasetId: number;
    treatmentId: number;
    groupIds: string;
    min: number;
    q1: number;
    median: number;
    q3: number;
    max: number;
    avg: number;
    count: number;
    xid: number;
}

export interface ServerSetupConfig {
    dbConfig: DatabaseConfig;
    gkConfig: GatekeeperConfig;
}

export interface SgoneGermplasmUnificationRequest {
    unifications: SgoneGermplasmUnification[];
}

export interface SubsettedDatasetRequest extends PaginatedRequest {
    datasetIds: number[];
    yids: number[];
    xids: number[];
    ygroupIds: number[];
    xgroupIds: number[];
}

export interface SubsettedGenotypeDatasetRequest extends SubsettedDatasetRequest {
    mapId: number;
    generateFlapjackProject: boolean;
    generateHapMap: boolean;
    generateFlatFile: boolean;
    fileTypes: AdditionalExportFormat[];
}

export interface Token {
    token: string;
    imageToken: string;
    id: number;
    username: string;
    fullName: string;
    email: string;
    userType: string;
    lifetime: number;
    createdOn: number;
}

export interface TraitDatasetRequest extends DatasetRequest {
    traitIds: number[];
}

export interface TraitDatasetStats {
    datasets: ViewTableDatasets[];
    traits: ViewTableTraits[];
    treatments: Treatments[];
    stats: Quantiles[];
}

export interface TraitStats {
    variableId: number;
    variableName: string;
    traitId: number;
    traitName: string;
    traitNameShort: string;
    min: number;
    avg: number;
    max: number;
    count: number;
    categories: string[][];
    dataType: string;
}

export interface TraitTimelineRequest {
    datasetIds: number[];
    traitIds: number[];
    groupIds: number[];
    markedIds: number[];
}

export interface TraitUnificationRequest {
    preferredTraitId: number;
    otherTraitIds: number[];
}

export interface TrialCreationDetails {
    datasetId: number;
    plots: PlotDetails[];
}

export interface TrialSetupStats {
    reps: string[];
    treatments: Treatments[];
    plots: Plot[];
}

export interface TrialsExportDatasetRequest extends PaginatedRequest {
    traitIds: number[];
    germplasmIds: number[];
    germplasmGroupIds: number[];
    datasetIds: number[];
}

export interface UnacceptedLicenseRequest extends PaginatedRequest {
    justUnacceptedLicenses: boolean;
}

export interface UserGroupModificationRequest {
    userGroupId: number;
    userIds: number[];
    addOperation: boolean;
}

export interface UuidRequest {
    uuids: string[];
}

export interface ViewMcpd {
    id: number;
    puid: string;
    instcode: string;
    accenumb: string;
    collnumb: string;
    collcode: string;
    collname: string;
    collinstaddress: string;
    collmissid: string;
    genus: string;
    species: string;
    spauthor: string;
    subtaxa: string;
    subtauthor: string;
    cropname: string;
    accename: string;
    acqdate: string;
    origcty: string;
    collsite: string;
    declatitude: number;
    latitude: any;
    declongitude: number;
    longitude: any;
    coorduncert: number;
    coorddatum: string;
    georefmeth: string;
    elevation: number;
    colldate: string;
    bredcode: string;
    bredname: string;
    sampstat: number;
    ancest: string;
    collsrc: number;
    donorcode: string;
    donorname: string;
    donornumb: string;
    othernumb: string;
    duplsite: string;
    duplinstname: string;
    storage: string;
    mlsstat: number;
    remarks: string;
    entitytype: string;
    entityparentid: number;
    entityparentaccenumb: string;
}

export interface ViewTableClimateDataWithGroups extends ViewTableClimateData {
    groups: Groups[];
}

export interface ViewTableExperiments {
    projectId: number;
    experimentId: number;
    experimentName: string;
    experimentDescription: string;
    experimentDate: Date;
    createdOn: Date;
    genotypeCount: number;
    trialsCount: number;
    alleleFreqCount: number;
    climateCount: number;
    compoundCount: number;
    pedigreeCount: number;
}

export interface ViewTableGermplasm {
    germplasmName: string;
    germplasmDisplayName: string;
    germplasmId: number;
    germplasmGid: string;
    germplasmNumber: string;
    germplasmPuid: string;
    entityTypeId: number;
    entityTypeName: string;
    entityParentId: number;
    entityParentName: string;
    entityParentGeneralIdentifier: string;
    biologicalStatusId: number;
    biologicalStatusName: string;
    synonyms: string[];
    collectorNumber: string;
    genus: string;
    species: string;
    subtaxa: string;
    institutions: GermplasmInstitution[];
    locationId: number;
    location: string;
    latitude: number;
    longitude: number;
    elevation: number;
    countryName: string;
    countryCode: string;
    collDate: string;
    pdci: number;
    imageCount: number;
    firstImagePath: string;
    hasTrialsData: number;
    hasGenotypicData: number;
    hasAllelefreqData: number;
    hasCompoundData: number;
    hasPedigreeData: number;
}

export interface ViewTableGroupGermplasm extends ViewTableGermplasm {
    groupId: number;
}

export interface ViewTableGroupLocations extends ViewTableLocations {
    groupId: number;
}

export interface ViewTableGroupMarkers extends ViewTableMarkers {
    groupId: number;
}

export interface ViewTablePublicationGermplasm extends ViewTableGermplasm {
    publicationId: number;
}

export interface ViewTableStoriesEnriched extends ViewTableStories {
    canAccess: boolean;
}

export interface ViewTableTrialGermplasm extends ViewTableGermplasm {
    groupIds: number[];
}

export interface ViewTableTrialsData {
    germplasmId: number;
    germplasmGid: string;
    germplasmName: string;
    germplasmDisplayName: string;
    germplasmSynonyms: string[];
    entityParentName: string;
    entityParentGeneralIdentifier: string;
    entityType: string;
    datasetId: number;
    datasetName: string;
    datasetDescription: string;
    locationName: string;
    countryName: string;
    countryCode2: string;
    variableId: number;
    variableName: string;
    variableDescription: string;
    traitId: number;
    traitName: string;
    traitAbbreviation: string;
    scaleRestrictions: TraitRestrictions;
    scaleDatatype: ScalesDatatype;
    scaleUnit: string;
    treatment: string;
    trialsetupId: number;
    rep: string;
    block: string;
    trialRow: number;
    trialColumn: number;
    groups: Groups[];
    latitude: number;
    longitude: number;
    elevation: number;
    recordingDate: Date;
    traitValue: string;
}

export interface ViewUserDetailsType extends ViewUserDetails {
    userType: UserType;
    userTypeString: string;
}

export interface Attributedata extends Serializable {
    id: number;
    attributeId: number;
    foreignId: number;
    value: string;
    createdOn: Date;
    updatedOn: Date;
}

export interface Attributes extends Serializable {
    id: number;
    name: string;
    description: string;
    datatype: AttributesDatatype;
    targetTable: string;
    createdOn: Date;
    updatedOn: Date;
}

export interface Biologicalstatus extends Serializable {
    id: number;
    sampstat: string;
    createdOn: Date;
    updatedOn: Date;
}

export interface Climatedata extends Serializable {
    id: number;
    climateId: number;
    locationId: number;
    climateValue: string;
    datasetId: number;
    recordingDate: Date;
    oldRecordingDate: string;
    createdOn: Date;
    updatedOn: Date;
}

export interface Climates extends Serializable {
    id: number;
    name: string;
    shortName: string;
    description: string;
    datatype: ClimatesDatatype;
    unitId: number;
    createdOn: Date;
    updatedOn: Date;
}

export interface Collaborators extends Serializable {
    id: number;
    firstName: string;
    lastName: string;
    email: string;
    phone: string;
    externalId: string;
    institutionId: number;
    createdOn: Date;
    updatedOn: Date;
}

export interface Collectingsources extends Serializable {
    id: number;
    collsrc: string;
    createdOn: Date;
    updatedOn: Date;
}

export interface Comments extends Serializable {
    id: number;
    commenttypeId: number;
    userId: number;
    visibility: boolean;
    description: string;
    referenceId: number;
    createdOn: Date;
    updatedOn: Date;
}

export interface Commenttypes extends Serializable {
    id: number;
    description: string;
    referenceTable: string;
    createdOn: Date;
    updatedOn: Date;
}

export interface Countries extends Serializable {
    id: number;
    countryCode2: string;
    countryCode3: string;
    countryName: string;
    createdOn: Date;
    updatedOn: Date;
}

export interface DataExportJobs extends Serializable {
    id: number;
    uuid: string;
    jobId: string;
    jobConfig: ExportJobDetails;
    userId: number;
    status: DataExportJobsStatus;
    visibility: boolean;
    datatype: DataExportJobsDatatype;
    datasetIds: number[];
    resultSize: number;
    createdOn: Date;
    updatedOn: Date;
}

export interface DataImportJobs extends Serializable {
    id: number;
    uuid: string;
    jobId: string;
    jobConfig: ImportJobDetails;
    userId: number;
    originalFilename: string;
    isUpdate: boolean;
    datasetstateId: number;
    datatype: DataImportJobsDatatype;
    status: DataImportJobsStatus;
    imported: boolean;
    visibility: boolean;
    feedback: ImportResult[];
    stats: ImportJobStats;
    createdOn: Date;
    updatedOn: Date;
}

export interface Datasetaccesslogs extends Serializable {
    id: number;
    userId: number;
    userName: string;
    userEmail: string;
    userInstitution: string;
    datasetId: number;
    reason: string;
    createdOn: Date;
    updatedOn: Date;
}

export interface Datasetcollaborators extends Serializable {
    id: number;
    datasetId: number;
    collaboratorId: number;
    collaboratorRoles: string;
    createdOn: Date;
    updatedOn: Date;
}

export interface Datasetfileresources extends Serializable {
    datasetId: number;
    fileresourceId: number;
    createdOn: Date;
    updatedOn: Date;
}

export interface Datasetlocations extends Serializable {
    datasetId: number;
    locationId: number;
    createdOn: Date;
    updatedOn: Date;
}

export interface Datasetmembers extends Serializable {
    id: number;
    datasetId: number;
    foreignId: number;
    datasetmembertypeId: number;
    createdOn: Date;
    updatedOn: Date;
}

export interface Datasetmembertypes extends Serializable {
    id: number;
    targetTable: string;
    createdOn: Date;
    updatedOn: Date;
}

export interface Datasetmeta extends Serializable {
    id: number;
    datasetId: number;
    nrOfDataObjects: number;
    nrOfDataPoints: number;
    createdOn: Date;
    updatedOn: Date;
}

export interface Datasetpermissions extends Serializable {
    id: number;
    datasetId: number;
    userId: number;
    groupId: number;
    createdOn: Date;
    updatedOn: Date;
}

export interface Datasets extends Serializable {
    id: number;
    experimentId: number;
    datasettypeId: number;
    name: string;
    description: string;
    dateStart: Date;
    dateEnd: Date;
    sourceFile: string;
    datatype: string;
    dublinCore: DublinCore;
    version: string;
    createdBy: number;
    datasetStateId: number;
    licenseId: number;
    isExternal: boolean;
    hyperlink: string;
    createdOn: Date;
    updatedOn: Date;
    contact: string;
}

export interface Datasetstates extends Serializable {
    id: number;
    name: string;
    description: string;
    createdOn: Date;
    updatedOn: Date;
}

export interface Datasettypes extends Serializable {
    id: number;
    description: string;
    createdOn: Date;
    updatedOn: Date;
}

export interface Datawarnings extends Serializable {
    id: number;
    description: string;
    category: DatawarningsCategory;
    createdOn: Date;
    updatedOn: Date;
}

export interface Entitytypes extends Serializable {
    id: number;
    name: string;
    description: string;
    createdOn: Date;
    updatedOn: Date;
}

export interface Experiments extends Serializable {
    id: number;
    experimentName: string;
    userId: number;
    projectId: number;
    description: string;
    experimentDate: Date;
    createdOn: Date;
    updatedOn: Date;
}

export interface Fileresources extends Serializable {
    id: number;
    name: string;
    path: string;
    description: string;
    filesize: number;
    fileresourcetypeId: number;
    projectId: number;
    createdOn: Date;
    updatedOn: Date;
}

export interface Fileresourcetypes extends Serializable {
    id: number;
    name: string;
    description: string;
    createdOn: Date;
    updatedOn: Date;
}

export interface Germinatebase extends Serializable {
    id: number;
    generalIdentifier: string;
    number: string;
    name: string;
    displayName: string;
    bankNumber: string;
    taxonomyId: number;
    plantPassport: string;
    locationId: number;
    entitytypeId: number;
    entityparentId: number;
    pdci: number;
    createdOn: Date;
    updatedOn: Date;
}

export interface Germplasmdatawarnings extends Serializable {
    germinatebaseId: number;
    datawarningId: number;
    createdOn: Date;
    updatedOn: Date;
}

export interface Germplasminstitutions extends Serializable {
    germinatebaseId: number;
    institutionId: number;
    type: GermplasminstitutionsType;
    createdOn: Date;
    updatedOn: Date;
}

export interface Groupmembers extends Serializable {
    id: number;
    foreignId: number;
    groupId: number;
    createdOn: Date;
    updatedOn: Date;
}

export interface Groups extends Serializable {
    id: number;
    grouptypeId: number;
    name: string;
    description: string;
    visibility: boolean;
    createdBy: number;
    createdOn: Date;
    updatedOn: Date;
}

export interface Grouptypes extends Serializable {
    id: number;
    description: string;
    targetTable: string;
    createdOn: Date;
    updatedOn: Date;
}

export interface ImageToTags extends Serializable {
    imageId: number;
    imagetagId: number;
    createdOn: Date;
    updatedOn: Date;
}

export interface Images extends Serializable {
    id: number;
    imagetypeId: number;
    description: string;
    foreignId: number;
    path: string;
    exif: Exif;
    isReference: boolean;
    createdOn: Date;
    updatedOn: Date;
}

export interface Imagetags extends Serializable {
    id: number;
    tagName: string;
    createdOn: Date;
    updatedOn: Date;
}

export interface Imagetypes extends Serializable {
    id: number;
    description: string;
    referenceTable: string;
    createdOn: Date;
    updatedOn: Date;
}

export interface Institutions extends Serializable {
    id: number;
    code: string;
    name: string;
    acronym: string;
    countryId: number;
    contact: string;
    phone: string;
    email: string;
    address: string;
    createdOn: Date;
    updatedOn: Date;
}

export interface Licensedata extends Serializable {
    id: number;
    licenseId: number;
    localeId: number;
    content: string;
    createdOn: Date;
    updatedOn: Date;
}

export interface Licenselogs extends Serializable {
    id: number;
    licenseId: number;
    userId: number;
    acceptedOn: Date;
}

export interface Licenses extends Serializable {
    id: number;
    name: string;
    description: string;
    createdOn: Date;
    updatedOn: Date;
}

export interface Links extends Serializable {
    id: number;
    linktypeId: number;
    foreignId: number;
    hyperlink: string;
    description: string;
    visibility: boolean;
    createdOn: Date;
    updatedOn: Date;
}

export interface Linktypes extends Serializable {
    id: number;
    description: string;
    targetTable: string;
    targetColumn: string;
    placeholder: string;
    createdOn: Date;
    updatedOn: Date;
}

export interface Locales extends Serializable {
    id: number;
    name: string;
    description: string;
    createdOn: Date;
    updatedOn: Date;
}

export interface Locations extends Serializable {
    id: number;
    locationtypeId: number;
    countryId: number;
    state: string;
    region: string;
    siteName: string;
    siteNameShort: string;
    elevation: number;
    latitude: number;
    longitude: number;
    coordinateUncertainty: number;
    coordinateDatum: string;
    georeferencingMethod: string;
    createdOn: Date;
    updatedOn: Date;
}

export interface Locationtypes extends Serializable {
    id: number;
    name: string;
    description: string;
    createdOn: Date;
    updatedOn: Date;
}

export interface Mapdefinitions extends Serializable {
    id: number;
    mapfeaturetypeId: number;
    markerId: number;
    mapId: number;
    definitionStart: number;
    definitionEnd: number;
    chromosome: string;
    armImpute: string;
    createdOn: Date;
    updatedOn: Date;
}

export interface Mapfeaturetypes extends Serializable {
    id: number;
    description: string;
    createdOn: Date;
    updatedOn: Date;
}

export interface Mapoverlays extends Serializable {
    id: number;
    name: string;
    description: string;
    bottomLeftLat: number;
    bottomLeftLng: number;
    topRightLat: number;
    topRightLng: number;
    isLegend: boolean;
    referenceTable: MapoverlaysReferenceTable;
    foreignId: number;
    datasetId: number;
    recordingDate: Date;
    createdOn: Date;
    updatedOn: Date;
}

export interface Maps extends Serializable {
    id: number;
    name: string;
    description: string;
    visibility: boolean;
    createdOn: Date;
    updatedOn: Date;
    userId: number;
}

export interface Markers extends Serializable {
    id: number;
    markertypeId: number;
    markerName: string;
    createdOn: Date;
    updatedOn: Date;
}

export interface Markertypes extends Serializable {
    id: number;
    description: string;
    createdOn: Date;
    updatedOn: Date;
}

export interface Mcpd extends Serializable {
    germinatebaseId: number;
    puid: string;
    instcode: string;
    accenumb: string;
    collnumb: string;
    collcode: string;
    collname: string;
    collinstaddress: string;
    collmissid: string;
    genus: string;
    species: string;
    spauthor: string;
    subtaxa: string;
    subtauthor: string;
    cropname: string;
    accename: string;
    acqdate: string;
    origcty: string;
    collsite: string;
    declatitude: number;
    latitude: string;
    declongitude: number;
    longitude: string;
    coorduncert: number;
    coorddatum: string;
    georefmeth: string;
    elevation: number;
    colldate: string;
    bredcode: string;
    bredname: string;
    sampstat: number;
    ancest: string;
    collsrc: number;
    donorcode: string;
    donorname: string;
    donornumb: string;
    othernumb: string;
    duplsite: string;
    duplinstname: string;
    storage: string;
    mlsstat: number;
    remarks: string;
    createdOn: Date;
    updatedOn: Date;
}

export interface Methodontologies extends Serializable {
    ontologyId: number;
    methodId: number;
    ontologyPuid: string;
    createdOn: Date;
    updatedOn: Date;
}

export interface Methods extends Serializable {
    id: number;
    name: string;
    description: string;
    methodClass: MethodsMethodClass;
    setsize: number;
    isTimeseries: boolean;
    createdOn: Date;
    updatedOn: Date;
}

export interface Mlsstatus extends Serializable {
    id: number;
    description: string;
    createdOn: Date;
    updatedOn: Date;
}

export interface News extends Serializable {
    id: number;
    newstypeId: number;
    title: string;
    content: string;
    image: string;
    imageFit: NewsImageFit;
    hyperlink: string;
    userId: number;
    createdOn: Date;
    updatedOn: Date;
}

export interface Newstypes extends Serializable {
    id: number;
    name: string;
    description: string;
    createdOn: Date;
    updatedOn: Date;
}

export interface Ontologies extends Serializable {
    id: number;
    name: string;
    description: string;
    version: string;
    url: string;
    createdOn: Date;
    updatedOn: Date;
}

export interface Pedigreedefinitions extends Serializable {
    id: number;
    datasetId: number;
    germinatebaseId: number;
    pedigreenotationId: number;
    pedigreedescriptionId: number;
    definition: string;
    createdOn: Date;
    updatedOn: Date;
}

export interface Pedigreedescriptions extends Serializable {
    id: number;
    name: string;
    description: string;
    author: string;
    createdOn: Date;
    updatedOn: Date;
}

export interface Pedigreenotations extends Serializable {
    id: number;
    name: string;
    description: string;
    referenceUrl: string;
    createdOn: Date;
    updatedOn: Date;
}

export interface Pedigrees extends Serializable {
    id: number;
    datasetId: number;
    germinatebaseId: number;
    parentId: number;
    relationshipType: PedigreesRelationshipType;
    pedigreedescriptionId: number;
    relationshipDescription: string;
    createdOn: Date;
    updatedOn: Date;
}

export interface Phenotypedata extends Serializable {
    id: number;
    trialsetupId: number;
    variableId: number;
    phenotypeValue: string;
    recordingDate: Date;
    createdOn: Date;
    updatedOn: Date;
}

export interface Projectcollaborators extends Serializable {
    collaboratorId: number;
    projectId: number;
    role: ProjectcollaboratorsRole;
    createdOn: Date;
    updatedOn: Date;
}

export interface Projectgroups extends Serializable {
    projectId: number;
    groupId: number;
    createdOn: Date;
    updatedOn: Date;
}

export interface Projectpublications extends Serializable {
    projectId: number;
    publicationId: number;
    createdOn: Date;
    updatedOn: Date;
}

export interface Projects extends Serializable {
    id: number;
    name: string;
    description: string;
    pageContent: string;
    externalUrl: string;
    imageId: number;
    startDate: Date;
    endDate: Date;
    createdOn: Date;
    updatedOn: Date;
}

export interface Publicationdata extends Serializable {
    id: number;
    foreignId: number;
    publicationId: number;
    referenceType: PublicationdataReferenceType;
    createdOn: Date;
    updatedOn: Date;
}

export interface Publications extends Serializable {
    id: number;
    doi: string;
    fallbackCache: string;
    createdOn: Date;
    updatedOn: Date;
}

export interface Scaleontologies extends Serializable {
    ontologyId: number;
    scaleId: number;
    ontologyPuid: string;
    createdOn: Date;
    updatedOn: Date;
}

export interface Scales extends Serializable {
    id: number;
    name: string;
    description: string;
    unit: string;
    datatype: ScalesDatatype;
    restrictions: JSON;
    createdOn: Date;
    updatedOn: Date;
}

export interface SchemaVersion extends Serializable {
    installedRank: number;
    version: string;
    description: string;
    type: string;
    script: string;
    checksum: number;
    installedBy: string;
    installedOn: Date;
    executionTime: number;
    success: boolean;
}

export interface Stories extends Serializable {
    id: number;
    name: string;
    description: string;
    imageId: number;
    requirements: StoryRequirements;
    publicationId: number;
    projectId: number;
    featured: boolean;
    visibility: boolean;
    userId: number;
    createdOn: Date;
    updatedOn: Date;
}

export interface Storysteps extends Serializable {
    id: number;
    storyId: number;
    storyIndex: number;
    pageConfig: StoryStepConfig;
    name: string;
    description: string;
    imageId: number;
    createdOn: Date;
    updatedOn: Date;
}

export interface Synonyms extends Serializable {
    id: number;
    foreignId: number;
    synonymtypeId: number;
    synonyms: string[];
    createdOn: Date;
    updatedOn: Date;
}

export interface Synonymtypes extends Serializable {
    id: number;
    targetTable: string;
    name: string;
    description: string;
    createdOn: Date;
    updatedOn: Date;
}

export interface Taxonomies extends Serializable {
    id: number;
    genus: string;
    species: string;
    subtaxa: string;
    speciesAuthor: string;
    subtaxaAuthor: string;
    cropname: string;
    ploidy: number;
    createdOn: Date;
    updatedOn: Date;
}

export interface Taxonomyproviders extends Serializable {
    id: number;
    name: string;
    homepageUrl: string;
    taxonomyPlaceholderUrl: string;
    createdOn: Date;
    updatedOn: Date;
}

export interface Taxonomyproviderslinks extends Serializable {
    taxonomyId: number;
    taxonomyproviderId: number;
    externalId: string;
}

export interface Traitcategories extends Serializable {
    id: number;
    name: string;
    description: string;
    createdOn: Date;
    updatedOn: Date;
}

export interface Traitontologies extends Serializable {
    ontologyId: number;
    traitId: number;
    ontologyPuid: string;
    createdOn: Date;
    updatedOn: Date;
}

export interface Traits extends Serializable {
    id: number;
    name: string;
    description: string;
    abbreviation: string;
    traitClass: TraitsTraitClass;
    traitcategoryId: number;
    synonyms: string[];
    createdOn: Date;
    updatedOn: Date;
}

export interface Treatments extends Serializable {
    id: number;
    name: string;
    description: string;
    createdOn: Date;
    updatedOn: Date;
}

export interface Trialseries extends Serializable {
    id: number;
    seriesname: string;
    createdOn: Date;
    updatedOn: Date;
}

export interface Trialsetup extends Serializable {
    id: number;
    germinatebaseId: number;
    datasetId: number;
    locationId: number;
    treatmentId: number;
    trialseriesId: number;
    block: string;
    rep: string;
    trialRow: number;
    trialColumn: number;
    latitude: number;
    longitude: number;
    elevation: number;
    createdOn: Date;
    updatedOn: Date;
}

export interface Units extends Serializable {
    id: number;
    unitName: string;
    unitAbbreviation: string;
    unitDescription: string;
    createdOn: Date;
    updatedOn: Date;
}

export interface Userfeedback extends Serializable {
    id: number;
    content: string;
    image: any;
    pageUrl: string;
    userId: number;
    contactEmail: string;
    feedbackType: UserfeedbackFeedbackType;
    severity: UserfeedbackSeverity;
    isNew: boolean;
    createdOn: Date;
    updatedOn: Date;
}

export interface Usergroupmembers extends Serializable {
    id: number;
    userId: number;
    usergroupId: number;
    createdOn: Date;
    updatedOn: Date;
}

export interface Usergroups extends Serializable {
    id: number;
    name: string;
    description: string;
    createdOn: Date;
    updatedOn: Date;
}

export interface Variableontologies extends Serializable {
    ontologyId: number;
    variableId: number;
    ontologyPuid: string;
    createdOn: Date;
    updatedOn: Date;
}

export interface Variables extends Serializable {
    id: number;
    name: string;
    description: string;
    traitId: number;
    methodId: number;
    scaleId: number;
    createdOn: Date;
    updatedOn: Date;
}

export interface ViewStatsBiologicalstatus extends Serializable {
    biologicalstatus: string;
    genus: string;
    count: number;
}

export interface ViewStatsCountry extends Serializable {
    country: string;
    code: string;
    count: number;
}

export interface ViewStatsPdci extends Serializable {
    bin: string;
    count: number;
}

export interface ViewStatsTaxonomy extends Serializable {
    genus: string;
    species: string;
    subtaxa: string;
    count: number;
}

export interface ViewTableClimateData extends Serializable {
    locationId: number;
    locationName: string;
    locationRegion: string;
    locationState: string;
    locationType: string;
    locationLatitude: number;
    locationLongitude: number;
    locationElevation: number;
    countryName: string;
    countryCode2: string;
    countryCode3: string;
    datasetId: number;
    datasetName: string;
    datasetDescription: string;
    climateId: number;
    climateName: string;
    climateNameShort: string;
    climateDataType: ViewTableClimateDataClimateDataType;
    unitName: string;
    recordingDate: Date;
    climateValue: string;
}

export interface ViewTableClimates extends Serializable {
    climateId: number;
    climateName: string;
    climateNameShort: string;
    climateDescription: string;
    dataType: ViewTableClimatesDataType;
    unitId: number;
    unitName: string;
    unitDescription: string;
    overlays: number;
    unitAbbreviation: string;
    count: number;
}

export interface ViewTableCollaborators extends Serializable {
    collaboratorId: number;
    collaboratorFirstName: string;
    collaboratorLastName: string;
    collaboratorExternalId: string;
    collaboratorEmail: string;
    collaboratorPhone: string;
    collaboratorRoles: string;
    institutionId: number;
    institutionName: string;
    institutionAddress: string;
    datasetId: number;
    countryId: number;
    countryName: string;
    countryCode2: string;
    countryCode3: string;
    projectIds: number[];
}

export interface ViewTableComments extends Serializable {
    commentId: number;
    commentTypeId: number;
    commentType: string;
    commentForeignId: number;
    userId: number;
    userName: string;
    commentContent: string;
    updatedOn: Date;
}

export interface ViewTableDatasetAttributes extends Serializable {
    datasetId: number;
    datasetName: string;
    datasetDescription: string;
    attributeId: number;
    attributeName: string;
    attributeDescription: string;
    attributeType: ViewTableDatasetAttributesAttributeType;
    targetTable: string;
    foreignId: number;
    attributeValue: string;
}

export interface ViewTableDatasets extends Serializable {
    datasetId: number;
    datasetName: string;
    datasetDescription: string;
    hyperlink: string;
    sourceFile: string;
    version: string;
    datasetType: string;
    experimentId: number;
    experimentName: string;
    experimentDescription: string;
    projectId: number;
    projectName: string;
    projectDescription: string;
    datatype: string;
    datasetState: string;
    locations: ViewTableLocations[];
    institutions: ViewTableInstitutions[];
    licenseId: number;
    licenseName: string;
    contact: string;
    startDate: Date;
    endDate: Date;
    dublinCore: DublinCore;
    createdOn: Date;
    updatedOn: Date;
    dataObjectCount: number;
    dataPointCount: number;
    isExternal: boolean;
    publications: number;
    fileresourceIds: number[];
    collaborators: number;
    attributes: number;
    acceptedBy: number[];
}

export interface ViewTableEntities extends Serializable {
    entityParentId: number;
    entityParentGid: string;
    entityParentName: string;
    entityParentDisplayName: string;
    entityParentType: string;
    entityChildId: number;
    entityChildGid: string;
    entityChildName: string;
    entityChildDisplayName: string;
    entityChildType: string;
}

export interface ViewTableFileresources extends Serializable {
    fileresourceId: number;
    fileresourceName: string;
    fileresourcePath: string;
    fileresourceDescription: string;
    fileresourceSize: number;
    fileresourceCreatedOn: Date;
    fileresourceUpdatedOn: Date;
    projectId: number;
    projectName: string;
    projectDescription: string;
    fileresourcetypeId: number;
    fileresourcetypeName: string;
    fileresourcetypeDescription: string;
    datasetIds: number[];
}

export interface ViewTableFileresourcetypes extends Serializable {
    id: number;
    name: string;
    description: string;
    createdOn: Date;
    updatedOn: Date;
    count: number;
}

export interface ViewTableGermplasmAttributes extends Serializable {
    attributeValueId: number;
    germplasmId: number;
    germplasmGid: string;
    germplasmName: string;
    germplasmDisplayName: string;
    attributeId: number;
    attributeName: string;
    attributeDescription: string;
    attributeType: ViewTableGermplasmAttributesAttributeType;
    targetTable: string;
    foreignId: number;
    createdOn: Date;
    attributeValue: string;
}

export interface ViewTableGermplasmDeprecated extends Serializable {
    germplasmName: string;
    germplasmId: number;
    germplasmGid: string;
    germplasmNumber: string;
    gerplasmPuid: string;
    entityTypeId: number;
    entityTypeName: string;
    entityParentId: number;
    entityParentName: string;
    entityParentGeneralIdentifier: string;
    biologicalStatusId: number;
    biologicalStatusName: string;
    synonyms: string[];
    collectorNumber: string;
    genus: string;
    species: string;
    subtaxa: string;
    institutions: GermplasmInstitution[];
    location: string;
    latitude: number;
    longitude: number;
    elevation: number;
    countryName: string;
    countryCode: string;
    collDate: string;
    pdci: number;
    imageCount: number;
    firstImagePath: string;
    hasTrialsData: number;
    hasGenotypicData: number;
    hasAllelefreqData: number;
}

export interface ViewTableGroups extends Serializable {
    groupId: number;
    groupName: string;
    groupDescription: string;
    groupTypeId: number;
    groupType: string;
    userName: string;
    userId: number;
    groupVisibility: boolean;
    createdOn: Date;
    updatedOn: Date;
    projectIds: number[];
    count: number;
}

export interface ViewTableImages extends Serializable {
    imageId: number;
    imageDescription: string;
    imageForeignId: number;
    imageIsReference: boolean;
    imagePath: string;
    imageExif: Exif;
    imageType: string;
    imageRefTable: string;
    referenceName: string;
    createdOn: Date;
    tags: ImageTag[];
}

export interface ViewTableImportJobs extends Serializable {
    id: number;
    isUpdate: boolean;
    datasetstateId: number;
    datatype: ViewTableImportJobsDatatype;
    status: ViewTableImportJobsStatus;
    stats: ImportJobStats;
    createdOn: Date;
}

export interface ViewTableInstitutionDatasets extends Serializable {
    institutionId: number;
    institutionCode: string;
    institutionName: string;
    institutionAcronym: string;
    countryName: string;
    countryCode2: string;
    countryCode3: string;
    institutionContact: string;
    institutionPhone: string;
    institutionEmail: string;
    institutionAddress: string;
    allDatasetIds: number[];
    trialsDatasetIds: number[];
    genotypeDatasetIds: number[];
    climateDatasetIds: number[];
    pedigreeDatasetIds: number[];
}

export interface ViewTableInstitutions extends Serializable {
    institutionId: number;
    institutionName: string;
    institutionCode: string;
    institutionAcronym: string;
    institutionAddress: string;
    institutionEmail: string;
    institutionContact: string;
    institutionPhone: string;
    countryId: number;
    countryName: string;
    countryCode: string;
    institutionType: string;
}

export interface ViewTableLicenseDefinitions extends Serializable {
    licenseId: number;
    licenseName: string;
    licenseDescription: string;
    createdOn: Date;
    licenseData: { [index: string]: string };
}

export interface ViewTableLicenses extends Serializable {
    licenseId: number;
    licenseName: string;
    licenseDescription: string;
    licenseContent: { [index: string]: string };
    datasetId: number;
    acceptedBy: number[];
}

export interface ViewTableLinks extends Serializable {
    linkId: number;
    linkDescription: string;
    linkVisibility: boolean;
    linktypeId: number;
    linktypeDescription: string;
    linktypeTargetTable: string;
    linktypeTargetColumn: string;
    linkForeignId: number;
    hyperlink: string;
    placeholder: string;
    updatedOn: Date;
}

export interface ViewTableMapoverlays extends Serializable {
    mapoverlayId: number;
    mapoverlayName: string;
    mapoverlayDescription: string;
    mapoverlayBottomLeftLat: number;
    mapoverlayBottomLeftLng: number;
    mapoverlayTopRightLat: number;
    mapoverlayTopRightLng: number;
    mapoverlaysIsLegend: boolean;
    referenceTable: ViewTableMapoverlaysReferenceTable;
    foreignId: number;
    datasetId: number;
    datasetName: string;
    datasetDescription: string;
    datasetType: string;
    recordingDate: Date;
    createdOn: Date;
    updatedOn: Date;
}

export interface ViewTableMaps extends Serializable {
    mapId: number;
    mapName: string;
    mapDescription: string;
    userId: number;
    visibility: boolean;
    markerCount: number;
}

export interface ViewTableNews extends Serializable {
    newsId: number;
    newsTitle: string;
    newsContent: string;
    newsHyperlink: string;
    newsImage: string;
    newsImageFit: ViewTableNewsNewsImageFit;
    newstypeId: number;
    newstypeName: string;
    newstypeDescription: string;
    createdOn: Date;
    updatedOn: Date;
}

export interface ViewTablePedigreedefinitions extends Serializable {
    germplasmId: number;
    germplasmName: string;
    germplasmDisplayName: string;
    pedigreeNotationName: string;
    pedigreeNotationDescription: string;
    pedigreeNotationUrl: string;
    datasetId: number;
    datasetName: string;
    definitionId: number;
    definition: string;
    pedigreeDescriptionName: string;
    pedigreeDescriptionDescription: string;
    pedigreeDescriptionAuthor: string;
    createdOn: Date;
}

export interface ViewTablePedigrees extends Serializable {
    parentId: number;
    parentGid: string;
    parentName: string;
    parentDisplayName: string;
    parentNumber: string;
    childId: number;
    childGid: string;
    childName: string;
    childDisplayName: string;
    childNumber: string;
    datasetId: number;
    datasetName: string;
    experimentId: number;
    experimentName: string;
    relationshipType: ViewTablePedigreesRelationshipType;
    relationshipDescription: string;
    pedigreeDescription: string;
    pedigreeAuthor: string;
}

export interface ViewTableProjects extends Serializable {
    projectId: number;
    projectName: string;
    projectDescription: string;
    projectPageContent: string;
    projectExternalUrl: string;
    projectImageId: number;
    projectStartDate: Date;
    projectEndDate: Date;
    projectCreatedOn: Date;
    projectUpdatedOn: Date;
    datasets: Dataset[];
}

export interface ViewTableStories extends Serializable {
    storyId: number;
    storyName: string;
    storyDescription: string;
    storyRequirements: StoryRequirements;
    storyImageId: number;
    storyImageName: string;
    projectId: number;
    projectName: string;
    projectDescription: string;
    storyFeatured: boolean;
    storyVisibility: boolean;
    storyUserId: number;
    publicationId: number;
    publicationDoi: string;
    storySteps: Storysteps[];
    storyCreatedOn: Date;
    storyUpdatedOn: Date;
}

export interface ViewTableTaxonomies extends Serializable {
    taxonomyId: number;
    taxonomyGenus: string;
    taxonomySpecies: string;
    taxonomySubtaxa: string;
    taxonomyCropname: string;
    taxonomyProviders: TaxonomyProviderInfo[];
    count: number;
}

export interface ViewTableTraitAttributes extends Serializable {
    attributeValueId: number;
    variableId: number;
    variableName: string;
    attributeId: number;
    attributeName: string;
    attributeDescription: string;
    attributeType: ViewTableTraitAttributesAttributeType;
    targetTable: string;
    foreignId: number;
    createdOn: Date;
    attributeValue: string;
}

export interface ViewTableTraits extends Serializable {
    variableId: number;
    variableName: string;
    variableDescription: string;
    traitId: number;
    traitName: string;
    traitDescription: string;
    methodId: number;
    methodName: string;
    methodDescription: string;
    methodClass: ViewTableTraitsMethodClass;
    scaleId: number;
    scaleName: string;
    scaleDescription: string;
    traitAbbreviation: string;
    traitClass: ViewTableTraitsTraitClass;
    traitSynonyms: string[];
    methodSetSize: number;
    methodIsTimeseries: boolean;
    scaleDatatype: ViewTableTraitsScaleDatatype;
    scaleRestrictions: TraitRestrictions;
    scaleUnit: string;
    datasetIds: number[];
    count: number;
}

export interface ViewTableTraitsTemplate extends Serializable {
    variableCropontologyId: string;
    variableName: string;
    variableDescription: string;
    traitCopontologyId: string;
    traitName: string;
    traitDescription: string;
    traitAbbreviation: string;
    traitClass: ViewTableTraitsTemplateTraitClass;
    traitCategory: string;
    methodCropontologyId: string;
    methodName: string;
    methodDescription: string;
    methodClass: ViewTableTraitsTemplateMethodClass;
    methodSetSize: number;
    methodIsTimeseries: boolean;
    scaleCropontologyId: string;
    scaleName: string;
    scaleDescription: string;
    scaleUnit: string;
    scaleDataType: ViewTableTraitsTemplateScaleDataType;
    scaleMinimum: JSON;
    scaleMaximum: JSON;
    scaleValidValues: JSON;
}

export interface ViewTableUsergroups extends Serializable {
    userGroupId: number;
    userGroupName: string;
    userGroupDescription: string;
    createdOn: Date;
    count: number;
}

export interface BinningConfig {
    binningMethod: string;
    binsLeft: number;
    binsRight: number;
    splitPoint: number;
}

export interface Dataset {
    datasetId: number;
    datasetName: string;
    datasetType: string;
    datasetIsExternal: number;
}

export interface DbObjectCount {
    key: string;
    count: number;
}

export interface DublinCore {
    title: string[];
    creator: string[];
    subject: string[];
    description: string[];
    publisher: string[];
    contributor: string[];
    date: string[];
    type: string[];
    format: string[];
    identifier: string[];
    source: string[];
    language: string[];
    relation: string[];
    coverage: string[];
    rights: string[];
}

export interface Exif {
    apertureValue: string;
    cameraMake: string;
    cameraModel: string;
    colorSpace: string;
    compression: string;
    contrast: string;
    dateTime: Date;
    dateTimeOriginal: Date;
    dateTimeDigitized: Date;
    digitalZoomRatio: string;
    exifImageHeight: string;
    exifImageWidth: string;
    exifVersion: string;
    exposure: string;
    exposureBiasValue: string;
    exposureMode: string;
    exposureProgram: string;
    exposureTime: string;
    fileSource: string;
    flash: string;
    focalLength: string;
    gainControl: string;
    gpsAltitude: number;
    gpsLatitude: number;
    gpsLongitude: number;
    gpsTimestamp: Date;
    imageHeight: string;
    imageWidth: string;
    isoSpeedRatings: string;
    lensMake: string;
    lensModel: string;
    meteringMode: string;
    orientation: string;
    orientationCode: number;
    photometricInterpretation: string;
    samplesPerPixel: string;
    saturation: string;
    sceneCaptureType: string;
    sceneType: string;
    sensingMethod: string;
    sharpness: string;
    shutterSpeedValue: string;
    userComment: string;
    whiteBalance: string;
    whiteBalanceMode: string;
    yresolution: string;
    xresolution: string;
    fnumber: string;
}

export interface ExportJobDetails {
    baseFolder: string;
    fileTypes: AdditionalExportFormat[];
    subsetId: number;
    fileHeaders: string;
    binningConfig: BinningConfig;
    exportParams: string[];
    yids: number[];
    xids: number[];
    ygroupIds: number[];
    xgroupIds: number[];
}

export interface GermplasmInstitution {
    id: number;
    name: string;
    code: string;
    type: GermplasminstitutionsType;
}

export interface ImageTag {
    tagId: number;
    tagName: string;
}

export interface ImportJobDetails {
    baseFolder: string;
    dataFilename: string;
    deleteOnFail: boolean;
    targetDatasetId: number;
    runType: RunType;
    dataOrientation: DataOrientation;
}

export interface ImportJobStats {
    fileResourceId: number;
    datasetId: number;
    germplasm: number;
    markers: number;
    images: number;
    traits: number;
    locations: number;
    climates: number;
    groups: number;
}

export interface ImportResult {
    status: ImportStatus;
    rowIndex: number;
    message: string;
    type: StatusType;
}

export interface RouterConfig {
    name: string;
    query: { [index: string]: JsonElement };
    params: { [index: string]: string };
}

export interface StoryRequirements {
    datasetIds: number[];
    groupIds: number[];
}

export interface StoryStepConfig {
    router: RouterConfig;
    pageDetails: { [index: string]: string };
}

export interface TaxonCount {
    genus: LevelCount[];
    species: LevelCount[];
    subtaxa: LevelCount[];
}

export interface TaxonomyProviderInfo {
    providerName: string;
    providerHomepage: string;
    providerPlaceholder: string;
    externalId: string;
}

export interface TraitRestrictions {
    categories: string[][];
    min: number;
    max: number;
}

export interface ViewTableMapdefinitions {
    markerId: number;
    markerName: string;
    synonyms: string[];
    mapFeatureType: string;
    mapId: number;
    userId: number;
    visibility: boolean;
    mapName: string;
    chromosome: string;
    position: number;
}

export interface ViewTableMarkers {
    markerId: number;
    markerName: string;
    markerType: string;
    markerSynonyms: string[];
    createdOn: Date;
    updatedOn: Date;
}

export interface Serializable {
}

export interface AboutInfo {
    name: string;
    description: string;
    group: string;
    url: string;
    image: string;
}

export interface ImageConfig {
    name: string;
    text: string;
}

export interface Config {
    id: number;
    columnName: string;
    type: DataType;
    datasetIds: number[];
    markedIds: number[];
    groupIds: number[];
}

export interface Region {
    chromosome: string;
    start: number;
    end: number;
}

export interface Radius {
    markerId: number;
    left: number;
    right: number;
}

export interface NewUnapprovedUser extends LocaleRequest {
    id: number;
    userUsername: string;
    userPassword: string;
    userFullName: string;
    userEmailAddress: string;
    institutionId: number;
    institutionName: string;
    institutionAcronym: string;
    institutionAddress: string;
    databaseSystemId: number;
    needsApproval: number;
    unapprovedUser: UnapprovedUsers;
}

export interface SgoneGermplasmUnification {
    preferred: SgonePojo;
    others: SgonePojo[];
}

export interface PlotDetails {
    row: number;
    column: number;
    germplasm: string;
    rep: string;
}

export interface Plot {
    row: number;
    column: number;
}

export interface ViewUserDetails extends Serializable {
    id: number;
    username: string;
    fullName: string;
    emailAddress: string;
    lastLogin: Date;
    createdOn: Date;
    gatekeeperAccess: number;
    name: string;
    acronym: string;
    address: string;
}

export interface JSON extends Data {
}

export interface JsonElement {
    asInt: number;
    asDouble: number;
    asLong: number;
    asBoolean: boolean;
    asString: string;
    jsonArray: boolean;
    asJsonObject: JsonObject;
    jsonObject: boolean;
    asNumber: number;
    asBigDecimal: number;
    asJsonNull: JsonNull;
    asJsonPrimitive: JsonPrimitive;
    asBigInteger: number;
    jsonPrimitive: boolean;
    /**
     * @deprecated
     */
    asCharacter: string;
    asJsonArray: JsonArray;
    jsonNull: boolean;
    asFloat: number;
    asByte: number;
    asShort: number;
}

export interface LevelCount {
    taxonomy: string;
    count: number;
}

export interface Locale extends Cloneable, Serializable {
}

export interface LocaleRequest {
    locale: string;
    javaLocale: Locale;
}

export interface SgonePojo {
    id: string;
    name: string;
}

export interface Data extends Serializable {
}

export interface JsonObject extends JsonElement {
    empty: boolean;
}

export interface JsonNull extends JsonElement {
}

export interface JsonPrimitive extends JsonElement {
    number: boolean;
    boolean: boolean;
    string: boolean;
}

export interface JsonArray extends JsonElement, Iterable<JsonElement> {
    empty: boolean;
}

export interface Cloneable {
}

export interface Iterable<T> {
}

export const enum UserType {
    ADMIN = 'ADMIN',
    DATA_CURATOR = 'DATA_CURATOR',
    AUTH_USER = 'AUTH_USER',
    UNKNOWN = 'UNKNOWN',
}

export const enum AuthenticationMode {
    FULL = 'FULL',
    SELECTIVE = 'SELECTIVE',
    NONE = 'NONE',
}

export const enum DataImportMode {
    NONE = 'NONE',
    VERIFY = 'VERIFY',
    IMPORT = 'IMPORT',
}

export const enum FilterComparator {
    isNull = 'isNull',
    isNotNull = 'isNotNull',
    equals = 'equals',
    contains = 'contains',
    between = 'between',
    greaterThan = 'greaterThan',
    greaterOrEquals = 'greaterOrEquals',
    lessThan = 'lessThan',
    startsWith = 'startsWith',
    endsWith = 'endsWith',
    lessOrEquals = 'lessOrEquals',
    jsonSearch = 'jsonSearch',
    arrayContains = 'arrayContains',
    inSet = 'inSet',
}

export const enum FilterOperator {
    and = 'and',
    or = 'or',
}

export const enum ServerProperty {
    AUTHENTICATION_MODE = 'AUTHENTICATION_MODE',
    BCRYPT_SALT = 'BCRYPT_SALT',
    BRAPI_ENABLED = 'BRAPI_ENABLED',
    COLORS_CHART = 'COLORS_CHART',
    COLORS_TEMPLATE = 'COLORS_TEMPLATE',
    COLORS_GRADIENT = 'COLORS_GRADIENT',
    COLOR_PRIMARY = 'COLOR_PRIMARY',
    COMMENTS_ENABLED = 'COMMENTS_ENABLED',
    DASHBOARD_CATEGORIES = 'DASHBOARD_CATEGORIES',
    DASHBOARD_SECTIONS = 'DASHBOARD_SECTIONS',
    DATA_DIRECTORY_EXTERNAL = 'DATA_DIRECTORY_EXTERNAL',
    DATA_IMPORT_MODE = 'DATA_IMPORT_MODE',
    DATABASE_SERVER = 'DATABASE_SERVER',
    DATABASE_NAME = 'DATABASE_NAME',
    DATABASE_USERNAME = 'DATABASE_USERNAME',
    DATABASE_PASSWORD = 'DATABASE_PASSWORD',
    DATABASE_PORT = 'DATABASE_PORT',
    DATABASE_BACKUP_EVERY_DAYS = 'DATABASE_BACKUP_EVERY_DAYS',
    DATABASE_BACKUP_MAX_SIZE = 'DATABASE_BACKUP_MAX_SIZE',
    DEBUG_IS_DEVELOPMENT = 'DEBUG_IS_DEVELOPMENT',
    EXTERNAL_LINK_IDENTIFIER = 'EXTERNAL_LINK_IDENTIFIER',
    EXTERNAL_LINK_TEMPLATE = 'EXTERNAL_LINK_TEMPLATE',
    GERMINATE_CLIENT_URL = 'GERMINATE_CLIENT_URL',
    GATEKEEPER_URL = 'GATEKEEPER_URL',
    GATEKEEPER_USERNAME = 'GATEKEEPER_USERNAME',
    GATEKEEPER_PASSWORD = 'GATEKEEPER_PASSWORD',
    GATEKEEPER_REGISTRATION_ENABLED = 'GATEKEEPER_REGISTRATION_ENABLED',
    GATEKEEPER_REGISTRATION_REQUIRES_APPROVAL = 'GATEKEEPER_REGISTRATION_REQUIRES_APPROVAL',
    GOOGLE_ANALYTICS_KEY = 'GOOGLE_ANALYTICS_KEY',
    HIDDEN_COLUMNS_GERMPLASM = 'HIDDEN_COLUMNS_GERMPLASM',
    HIDDEN_COLUMNS_GERMPLASM_ATTRIBUTES = 'HIDDEN_COLUMNS_GERMPLASM_ATTRIBUTES',
    HIDDEN_COLUMNS_IMAGES = 'HIDDEN_COLUMNS_IMAGES',
    HIDDEN_COLUMNS_CLIMATES = 'HIDDEN_COLUMNS_CLIMATES',
    HIDDEN_COLUMNS_CLIMATE_DATA = 'HIDDEN_COLUMNS_CLIMATE_DATA',
    HIDDEN_COLUMNS_COMMENTS = 'HIDDEN_COLUMNS_COMMENTS',
    HIDDEN_COLUMNS_FILERESOURCES = 'HIDDEN_COLUMNS_FILERESOURCES',
    HIDDEN_COLUMNS_MAPS = 'HIDDEN_COLUMNS_MAPS',
    HIDDEN_COLUMNS_MARKERS = 'HIDDEN_COLUMNS_MARKERS',
    HIDDEN_COLUMNS_MAP_DEFIITIONS = 'HIDDEN_COLUMNS_MAP_DEFIITIONS',
    HIDDEN_COLUMNS_DATASETS = 'HIDDEN_COLUMNS_DATASETS',
    HIDDEN_COLUMNS_DATASET_ATTRIBUTES = 'HIDDEN_COLUMNS_DATASET_ATTRIBUTES',
    HIDDEN_COLUMNS_EXPERIMENTS = 'HIDDEN_COLUMNS_EXPERIMENTS',
    HIDDEN_COLUMNS_ENTITIES = 'HIDDEN_COLUMNS_ENTITIES',
    HIDDEN_COLUMNS_GROUPS = 'HIDDEN_COLUMNS_GROUPS',
    HIDDEN_COLUMNS_INSTITUTIONS = 'HIDDEN_COLUMNS_INSTITUTIONS',
    HIDDEN_COLUMNS_LOCATIONS = 'HIDDEN_COLUMNS_LOCATIONS',
    HIDDEN_COLUMNS_PEDIGREES = 'HIDDEN_COLUMNS_PEDIGREES',
    HIDDEN_COLUMNS_PEDIGREEDEFIITIONS = 'HIDDEN_COLUMNS_PEDIGREEDEFIITIONS',
    HIDDEN_COLUMNS_TRAITS = 'HIDDEN_COLUMNS_TRAITS',
    HIDDEN_COLUMNS_TRIALS_DATA = 'HIDDEN_COLUMNS_TRIALS_DATA',
    HIDDEN_COLUMNS_COLLABORATORS = 'HIDDEN_COLUMNS_COLLABORATORS',
    HIDDEN_COLUMNS_PUBLICATIONS = 'HIDDEN_COLUMNS_PUBLICATIONS',
    PLAUSIBLE_DOMAIN = 'PLAUSIBLE_DOMAIN',
    PLAUSIBLE_HASH_MODE = 'PLAUSIBLE_HASH_MODE',
    PLAUSIBLE_API_HOST = 'PLAUSIBLE_API_HOST',
    GRIDSCORE_URL = 'GRIDSCORE_URL',
    HELIUM_URL = 'HELIUM_URL',
    FIELDHUB_URL = 'FIELDHUB_URL',
    MYSQLDUMP_PATH = 'MYSQLDUMP_PATH',
    GRPD_NOTIFICATION_ENABLED = 'GRPD_NOTIFICATION_ENABLED',
    HIDDEN_PAGES = 'HIDDEN_PAGES',
    HIDDEN_PAGES_AUTODISCOVER = 'HIDDEN_PAGES_AUTODISCOVER',
    PDCI_ENABLED = 'PDCI_ENABLED',
    FILES_DELETE_AFTER_HOURS_ASYNC = 'FILES_DELETE_AFTER_HOURS_ASYNC',
    FILES_DELETE_AFTER_HOURS_TEMP = 'FILES_DELETE_AFTER_HOURS_TEMP',
    FEEDBACK_EMAIL = 'FEEDBACK_EMAIL',
    GENESYS_URL = 'GENESYS_URL',
    GENESYS_CLIENT_ID = 'GENESYS_CLIENT_ID',
    GENESYS_CLIENT_SECRET = 'GENESYS_CLIENT_SECRET',
    EMAIL_USERNAME = 'EMAIL_USERNAME',
    EMAIL_PASSWORD = 'EMAIL_PASSWORD',
    EMAIL_SERVER = 'EMAIL_SERVER',
    EMAIL_ADDRESS = 'EMAIL_ADDRESS',
    EMAIL_PORT = 'EMAIL_PORT',
    EMAIL_USE_TLS_1_2 = 'EMAIL_USE_TLS_1_2',
}

export const enum AdditionalExportFormat {
    text = 'text',
    flapjack = 'flapjack',
    hapmap = 'hapmap',
}

export const enum DataOrientation {
    GENOTYPE_GERMPLASM_BY_MARKER = 'GENOTYPE_GERMPLASM_BY_MARKER',
    GENOTYPE_MARKER_BY_GERMPLASM = 'GENOTYPE_MARKER_BY_GERMPLASM',
}

export const enum ImportStatus {
    GENERIC_DUPLICATE_COLUMN = 'GENERIC_DUPLICATE_COLUMN',
    GENERIC_IO_ERROR = 'GENERIC_IO_ERROR',
    GENERIC_MISSING_EXCEL_SHEET = 'GENERIC_MISSING_EXCEL_SHEET',
    GENERIC_MISSING_COLUMN = 'GENERIC_MISSING_COLUMN',
    GENERIC_MISSING_COUNTRY = 'GENERIC_MISSING_COUNTRY',
    GENERIC_MISSING_REQUIRED_VALUE = 'GENERIC_MISSING_REQUIRED_VALUE',
    GENERIC_MISSING_DB_ITEM_UPDATE = 'GENERIC_MISSING_DB_ITEM_UPDATE',
    GENERIC_VALUE_TOO_LONG = 'GENERIC_VALUE_TOO_LONG',
    GENERIC_INVALID_COUNTRY_CODE = 'GENERIC_INVALID_COUNTRY_CODE',
    GENERIC_INVALID_NUMBER = 'GENERIC_INVALID_NUMBER',
    GENERIC_INVALID_BOOLEAN = 'GENERIC_INVALID_BOOLEAN',
    GENERIC_INVALID_DATE = 'GENERIC_INVALID_DATE',
    GENERIC_INVALID_DATATYPE = 'GENERIC_INVALID_DATATYPE',
    GENERIC_INVALID_MARKER = 'GENERIC_INVALID_MARKER',
    GENERIC_INVALID_LOCATION = 'GENERIC_INVALID_LOCATION',
    GENERIC_INVALID_GERMPLASM = 'GENERIC_INVALID_GERMPLASM',
    GENERIC_DUPLICATE_VALUE = 'GENERIC_DUPLICATE_VALUE',
    GENERIC_INVALID_REFERENCE = 'GENERIC_INVALID_REFERENCE',
    GENERIC_DISPLAY_NAME_USED_BUT_NOT_UNIQUE = 'GENERIC_DISPLAY_NAME_USED_BUT_NOT_UNIQUE',
    GROUP_INVALID_GROUP_VISIBILITY = 'GROUP_INVALID_GROUP_VISIBILITY',
    GROUP_INVALID_CELL_VALUE = 'GROUP_INVALID_CELL_VALUE',
    GROUP_HEADER_MISMATCH = 'GROUP_HEADER_MISMATCH',
    MCPD_DUPLICATE_ACCENUMB = 'MCPD_DUPLICATE_ACCENUMB',
    MCPD_MISSING_FIELD = 'MCPD_MISSING_FIELD',
    MCPD_INVALID_SAMPSTAT = 'MCPD_INVALID_SAMPSTAT',
    MCPD_INVALID_COLLSRC = 'MCPD_INVALID_COLLSRC',
    MCPD_INVALID_DMS = 'MCPD_INVALID_DMS',
    MCPD_INVALID_MLSSTATUS = 'MCPD_INVALID_MLSSTATUS',
    MCPD_INVALID_STORAGE = 'MCPD_INVALID_STORAGE',
    MCPD_INVALID_ENTITY_TYPE = 'MCPD_INVALID_ENTITY_TYPE',
    MCPD_INVALID_ENTITY_PARENT_ACCENUMB = 'MCPD_INVALID_ENTITY_PARENT_ACCENUMB',
    MCPD_MISSING_ACCENUMB = 'MCPD_MISSING_ACCENUMB',
    MCPD_INVALID_DUPLINST_NAME_MAPPING = 'MCPD_INVALID_DUPLINST_NAME_MAPPING',
    TRIALS_INVALID_TRAIT_DATATYPE = 'TRIALS_INVALID_TRAIT_DATATYPE',
    TRIALS_INVALID_TRAIT_CATEGORIES = 'TRIALS_INVALID_TRAIT_CATEGORIES',
    TRIALS_MISSING_TRAIT_DECLARATION = 'TRIALS_MISSING_TRAIT_DECLARATION',
    TRIALS_DATA_DATE_HEADER_MISMATCH = 'TRIALS_DATA_DATE_HEADER_MISMATCH',
    TRIALS_DATA_DATE_IDENTIFIER_MISMATCH = 'TRIALS_DATA_DATE_IDENTIFIER_MISMATCH',
    TRIALS_DATA_VIOLATES_RESTRICTION = 'TRIALS_DATA_VIOLATES_RESTRICTION',
    TRIALS_ROW_COL_MISMATCH = 'TRIALS_ROW_COL_MISMATCH',
    TRIALS_DATA_REP_MISSING = 'TRIALS_DATA_REP_MISSING',
    COMPOUND_DATA_DATE_HEADER_MISMATCH = 'COMPOUND_DATA_DATE_HEADER_MISMATCH',
    COMPOUND_DATA_DATE_IDENTIFIER_MISMATCH = 'COMPOUND_DATA_DATE_IDENTIFIER_MISMATCH',
    COMPOUND_MISSING_COMPOUND_DECLARATION = 'COMPOUND_MISSING_COMPOUND_DECLARATION',
    CLIMATE_MISSING_CLIMATE_DECLARATION = 'CLIMATE_MISSING_CLIMATE_DECLARATION',
    CLIMATE_MISSING_LOCATION_DECLARATION = 'CLIMATE_MISSING_LOCATION_DECLARATION',
    GENOTYPE_MISSING_ROW = 'GENOTYPE_MISSING_ROW',
    GENOTYPE_HEADER_LENGTH_MISMATCH = 'GENOTYPE_HEADER_LENGTH_MISMATCH',
    GENOTYPE_HAPMAP_INCORRECT_HEADER = 'GENOTYPE_HAPMAP_INCORRECT_HEADER',
    GENOTYPE_HAPMAP_INCORRECT_ROW_LENGTH = 'GENOTYPE_HAPMAP_INCORRECT_ROW_LENGTH',
    IMAGE_TEMPLATE_MISSING = 'IMAGE_TEMPLATE_MISSING',
    IMAGE_IMAGE_MISSING = 'IMAGE_IMAGE_MISSING',
    IMAGE_DEFINITION_MISSING = 'IMAGE_DEFINITION_MISSING',
    SHAPEFILE_MISSING_SHP = 'SHAPEFILE_MISSING_SHP',
    SHAPEFILE_MISSING_FIELD = 'SHAPEFILE_MISSING_FIELD',
    SHAPEFILE_INVALID_ACCENUMB = 'SHAPEFILE_INVALID_ACCENUMB',
    SHAPEFILE_ROW_COL_GERMPLASM_CONFLICT = 'SHAPEFILE_ROW_COL_GERMPLASM_CONFLICT',
    SHAPEFILE_DUPLICATE_ROW_COL = 'SHAPEFILE_DUPLICATE_ROW_COL',
    SHAPEFILE_WARNING_MISSING_ACCENUMB = 'SHAPEFILE_WARNING_MISSING_ACCENUMB',
}

export const enum RunType {
    CHECK = 'CHECK',
    IMPORT = 'IMPORT',
    CHECK_AND_IMPORT = 'CHECK_AND_IMPORT',
}

export const enum BackupType {
    UPDATE = 'UPDATE',
    PERIODICAL = 'PERIODICAL',
    MANUAL = 'MANUAL',
}

export const enum ViewTableClimateDataClimateDataType {
    categorical = 'categorical',
    numeric = 'numeric',
    text = 'text',
    date = 'date',
}

export const enum ScalesDatatype {
    categorical = 'categorical',
    numeric = 'numeric',
    text = 'text',
    date = 'date',
}

export const enum AttributesDatatype {
    categorical = 'categorical',
    numeric = 'numeric',
    text = 'text',
    date = 'date',
}

export const enum ClimatesDatatype {
    categorical = 'categorical',
    numeric = 'numeric',
    text = 'text',
    date = 'date',
}

export const enum DataExportJobsStatus {
    waiting = 'waiting',
    running = 'running',
    failed = 'failed',
    completed = 'completed',
    cancelled = 'cancelled',
}

export const enum DataExportJobsDatatype {
    genotype = 'genotype',
    trials = 'trials',
    allelefreq = 'allelefreq',
    climate = 'climate',
    compound = 'compound',
    pedigree = 'pedigree',
    unknown = 'unknown',
    images = 'images',
}

export const enum DataImportJobsDatatype {
    mcpd = 'mcpd',
    trial = 'trial',
    compound = 'compound',
    genotype = 'genotype',
    pedigree = 'pedigree',
    groups = 'groups',
    climate = 'climate',
    images = 'images',
    shapefile = 'shapefile',
    geotiff = 'geotiff',
}

export const enum DataImportJobsStatus {
    waiting = 'waiting',
    running = 'running',
    failed = 'failed',
    completed = 'completed',
    cancelled = 'cancelled',
}

export const enum DatawarningsCategory {
    generic = 'generic',
    quality = 'quality',
    source = 'source',
    deprecated = 'deprecated',
    missing = 'missing',
    inaccuracy = 'inaccuracy',
}

export const enum GermplasminstitutionsType {
    collection = 'collection',
    maintenance = 'maintenance',
    breeding = 'breeding',
    duplicate = 'duplicate',
    donor = 'donor',
}

export const enum MapoverlaysReferenceTable {
    phenotypes = 'phenotypes',
    climates = 'climates',
}

export const enum MethodsMethodClass {
    measurement = 'measurement',
    estimation = 'estimation',
    counting = 'counting',
    computation = 'computation',
    prediction = 'prediction',
    description = 'description',
    classification = 'classification',
    other = 'other',
}

export const enum NewsImageFit {
    contain = 'contain',
    cover = 'cover',
}

export const enum PedigreesRelationshipType {
    M = 'M',
    F = 'F',
    OTHER = 'OTHER',
}

export const enum ProjectcollaboratorsRole {
    principal_investigator = 'principal_investigator',
    data_submitter = 'data_submitter',
    data_curator = 'data_curator',
    data_collector = 'data_collector',
    corresponding_author = 'corresponding_author',
    author = 'author',
}

export const enum PublicationdataReferenceType {
    database = 'database',
    dataset = 'dataset',
    germplasm = 'germplasm',
    group = 'group',
    experiment = 'experiment',
}

export const enum TraitsTraitClass {
    abiotic_stress = 'abiotic_stress',
    agronomic = 'agronomic',
    biochemical = 'biochemical',
    biotic_stress = 'biotic_stress',
    fertility = 'fertility',
    morphological = 'morphological',
    phenological = 'phenological',
    physiological = 'physiological',
    quality = 'quality',
    other = 'other',
}

export const enum UserfeedbackFeedbackType {
    question = 'question',
    data_error = 'data_error',
    general = 'general',
    bug = 'bug',
    feature_request = 'feature_request',
}

export const enum UserfeedbackSeverity {
    low = 'low',
    medium = 'medium',
    high = 'high',
}

export const enum ViewTableClimatesDataType {
    categorical = 'categorical',
    numeric = 'numeric',
    text = 'text',
    date = 'date',
}

export const enum ViewTableDatasetAttributesAttributeType {
    categorical = 'categorical',
    numeric = 'numeric',
    text = 'text',
    date = 'date',
}

export const enum ViewTableGermplasmAttributesAttributeType {
    categorical = 'categorical',
    numeric = 'numeric',
    text = 'text',
    date = 'date',
}

export const enum ViewTableImportJobsDatatype {
    mcpd = 'mcpd',
    trial = 'trial',
    compound = 'compound',
    genotype = 'genotype',
    pedigree = 'pedigree',
    groups = 'groups',
    climate = 'climate',
    images = 'images',
    shapefile = 'shapefile',
    geotiff = 'geotiff',
}

export const enum ViewTableImportJobsStatus {
    waiting = 'waiting',
    running = 'running',
    failed = 'failed',
    completed = 'completed',
    cancelled = 'cancelled',
}

export const enum ViewTableMapoverlaysReferenceTable {
    phenotypes = 'phenotypes',
    climates = 'climates',
}

export const enum ViewTableNewsNewsImageFit {
    contain = 'contain',
    cover = 'cover',
}

export const enum ViewTablePedigreesRelationshipType {
    M = 'M',
    F = 'F',
    OTHER = 'OTHER',
}

export const enum ViewTableTraitAttributesAttributeType {
    categorical = 'categorical',
    numeric = 'numeric',
    text = 'text',
    date = 'date',
}

export const enum ViewTableTraitsMethodClass {
    measurement = 'measurement',
    estimation = 'estimation',
    counting = 'counting',
    computation = 'computation',
    prediction = 'prediction',
    description = 'description',
    classification = 'classification',
    other = 'other',
}

export const enum ViewTableTraitsTraitClass {
    abiotic_stress = 'abiotic_stress',
    agronomic = 'agronomic',
    biochemical = 'biochemical',
    biotic_stress = 'biotic_stress',
    fertility = 'fertility',
    morphological = 'morphological',
    phenological = 'phenological',
    physiological = 'physiological',
    quality = 'quality',
    other = 'other',
}

export const enum ViewTableTraitsScaleDatatype {
    categorical = 'categorical',
    numeric = 'numeric',
    text = 'text',
    date = 'date',
}

export const enum ViewTableTraitsTemplateTraitClass {
    abiotic_stress = 'abiotic_stress',
    agronomic = 'agronomic',
    biochemical = 'biochemical',
    biotic_stress = 'biotic_stress',
    fertility = 'fertility',
    morphological = 'morphological',
    phenological = 'phenological',
    physiological = 'physiological',
    quality = 'quality',
    other = 'other',
}

export const enum ViewTableTraitsTemplateMethodClass {
    measurement = 'measurement',
    estimation = 'estimation',
    counting = 'counting',
    computation = 'computation',
    prediction = 'prediction',
    description = 'description',
    classification = 'classification',
    other = 'other',
}

export const enum ViewTableTraitsTemplateScaleDataType {
    categorical = 'categorical',
    numeric = 'numeric',
    text = 'text',
    date = 'date',
}

export const enum StatusType {
    WARNING = 'WARNING',
    ERROR = 'ERROR',
}

export const enum DataType {
    TRAIT = 'TRAIT',
    CLIMATE = 'CLIMATE',
    COMPOUND = 'COMPOUND',
    GERMPLASM_COLUMN = 'GERMPLASM_COLUMN',
}
