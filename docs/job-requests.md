## Job Requests APIs Overview

### Get job requests

Get all available job requests

**URL**: `/api/v1/job-requests`

**Method**: `GET`

#### Success Response

**Code**: `200 OK`

**Output Example**:

```json
[
    {
        "agent_id": "8ab86c18-3fae-4804-bfd9-c3d6e8f66260"
    }
]
```

#### Not Found Response

**Code**: `404 Not found`

**Output Example**:

```json
[]
```

### Create job request

Request a new job

**URL**: `/api/v1/job-requests`

**Method**: `POST`

**Body Example**:
```json
{
    "agent_id": "8ab86c18-3fae-4804-bfd9-c3d6e8f66260"
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
    "error": "Value does not match schema: {\"agent_id\" (not (instance? java.lang.String 2))}"
}
```

#### nu-agent or job not found

**Code**: `404 Not found`

**Output Example**:

```json
[]
```
