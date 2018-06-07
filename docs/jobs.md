## Jobs APIs Overview

### Get job

Get a job by its uuid

**URL**: `/api/v1/jobs/{uuid}`

**Method**: `GET`

#### Success Response

**Code**: `200 OK`

**Output Example**:

```json
{
    "id": "ed0e23ef-6c2b-430c-9b90-cd4f1ff74c88",
    "type": "bills-questions",
    "urgent": false
}
```

#### Not Found Response

**Code**: `404 Not found`

**Output Example**:

```json
{}
```

### Get jobs

Get all jobs

**URL**: `/api/v1/jobs`

**Method**: `GET`

#### Success Response

**Code**: `200 OK`

**Output Example**:

```json
[
    {
        "id": "ed0e23ef-6c2b-430c-9b90-cd4f1ff74c88",
        "type": "bills-questions",
        "urgent": false
    },
    {
        "id": "f26e890b-df8e-422e-a39c-7762aa0bac36",
        "type": "rewards-questions",
        "urgent": true
    }
]
```

#### Not Found Response

**Code**: `404 Not found`

**Output Example**:

```json
[]
```

### Create job

Create a new job

**URL**: `/api/v1/jobs`

**Method**: `POST`

**Body Example**:
```json
{
    "id": "ed0e23ef-6c2b-430c-9b90-cd4f1ff74c88",
    "type": "bills-questions",
    "urgent": false
}
```

#### Success Response

**Code**: `201 Created`

**Output Example**:

```json
{}
```

#### Invalid Body Response

**Code**: `400 Bad request`

**Output Example**:

```json
{
    "error": "Value does not match schema: {\"urgent\" (not (instance? java.lang.Boolean 1))}"
}
```
