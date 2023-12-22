/*
  UNALZ : read and extract module for ALZ format.

  LICENSE (zlib License)
  Copyright (C) 2004-2009 kippler@gmail.com , http://www.kipple.pe.kr

  This software is provided 'as-is', without any express or implied
  warranty.  In no event will the authors be held liable for any damages
  arising from the use of this software.

  Permission is granted to anyone to use this software for any purpose,
  including commercial applications, and to alter it and redistribute it
  freely, subject to the following restrictions:

  1. The origin of this software must not be misrepresented; you must not
     claim that you wrote the original software. If you use this software
     in a product, an acknowledgment in the product documentation would be
     appreciated but is not required.
  2. Altered source versions must be plainly marked as such, and must not be
     misrepresented as being the original software.
  3. This notice may not be removed or altered from any source distribution.

  �� ����Ʈ����� ��� ����� �Ǵ� ������ ������ ���� "�ִ� �״��" �����˴ϴ�. �� 
  � ��쿡�� �ۼ��ڴ� �� ����Ʈ������ ������� ���� ���ؿ� ���� å���� ���� �ʽ��ϴ�.

  ���� ���� ������ �ؼ��ϴ� ��쿡 ���Ͽ� ������� ���� ���α׷��� �����ϴ� ��� �뵵�� �� ����Ʈ��� 
  ����ϰ� �����Ӱ� ���� �� ������� �� �ִ� ������ �������Գ� �ο��˴ϴ�.

  1. �� ����Ʈ������ ��ó�� �߸� ǥ���ϰų� ���� ����Ʈ��� �ڽ��� �ۼ��ߴٰ� �����ؼ��� �� �˴ϴ�. ��ǰ�� 
     �� ����Ʈ��� ����ϴ� ��� �䱸 ������ �ƴ����� ��ǰ ������ ���� ������ �־� �ֽø� �����ϰڽ��ϴ�.
  2. ������ �ҽ� ������ �ݵ�� ��Ȯ�ϰ� ǥ�õǾ�� �ϸ� ���� ����Ʈ����� ���εǵ��� �߸� ǥ���ؼ��� �� �˴ϴ�.
  3. ��� �ҽ� ���� �� �� ������ �����ϰų� ������ �� �����ϴ�.

  =============================================================================================================

  ������� :
    - �ҽ��� ������ ã�Ұų�, �������� �����Ͽ��� ��� �̿� ���� ������ �˷��ָ� ���� ���ﲬ..
	- �ڽ��� ���α׷��� �� �ҽ��� ����Ͽ��� ��� ������ �������� �����ָ� ���� ���� �Ҳ�..

  �ҽ� ���� : 
	- .ALZ �� ������ �����ϱ� ���� ���. 
	- ALZ�� BZIP2����(�����̶�� �� �����Ѱ� �ƴϰ�, ����� CRC �������� ���� ũ�⸦ ���� ����)��, 
	  DEFLATE ���� �˰��� ZIP�� ������ ����� ���� ���� ������.
	  (bzip2 �� ���� 4.9x ���� ����Ͽ���, deflate �� 5.x ���� ����Ͽ���. 5.x ���ʹ� bzip2�� �ʹ� ������ ��� ����)
	- UnAlzBz2decompress.c �� UnAlzbzlib.c �� ������ bzip2 �ҽ����� alz ������ ������ ������
	  (deflate �� ������ �ȵǾ��� ������ �׳� zlib �� �ᵵ ������.. bzip2 �� ������ �Ǿ 
	  ������ bzip2 �ҽ��� �״�� ���� �ȵȴ�.)
	- �� �ҽ��� 4ĭ ���� ��� �Ͽ���.

  ���� ���� :
	2004/02/06	- http://www.wotsit.org/ ���� ZIP File Format Specification version 4.5 [PKWARE Inc.] �� 
				  �ٿ�ε� �޾Ƽ� �м�.
	2004/02/07	- ��ť��Ʈ�� unzip Ŭ���� ���� ����
	2004/02/08	- unzip Ŭ������ alzip ���˿� ���߾� ���� �� �׽�Ʈ
				- deflate, rawdata ���� ���� ����.
				- ���̾˷α� �ڽ� ������ ����.
	2004/02/08	- bzip2 ���� ����
	2004/03/01	- bzip2 ���� �Ϻ� ���� Ҭ
				- callback ����..
	2004/03/07	- ��ƿ �Լ� �߰� (ExtractCurrentFileToBuf())
	2004/10/03	- ���� ���� ���� ��� �߰� (FILE I/O �� ���� ���� Ŭ���� ����)
	            - 2GB �̻��� ���� ó�� ���� (WINDOWS ONLY)
	2004/10/22	- ���� �÷���(BSD/LINUX)������ ���� ����
				  (BSD/LINUX �� ��� 2GB ������ ���ϸ� ����)
				- unalz 0.20
	2004/10/23	- by xxfree86 : DARWIN ������ ����, ��θ� "\\" ���Խ� ������ ����
	2004/10/24	- by aqua0125 : �ڵ������� ��ȯó��, 64bit ���� ó��
				- �򿣵��, �ڵ������� ��ȯ ���� �ҽ� ����
	2004/10/25	- by yongari : __LP64__ , �򿣵��(le64toh/le132oh/le16toh) ���� �̽� ����
	2004/10/26	- BSD/LINUX : byte-order, libiconv �̽� ���� 
				- unalz 0.22
	2004/10/30	- ���� & ����.. 
				- unalz 0.23
	2004/11/14	- by xxfree86 : ��ȣ �ɸ� ���� ó�� �߰�
				- unalz 0.30
	2004/11/27	- cygwin���� ������ �ǵ��� ����
	            - ��ȣó�� �κп� �Ϻ� ���� GPL �� CZipArchive �ڵ带 "ZIP File Format Specification version 4.5" ������ �����ؼ� �ٽ� �ڵ� & ����
				- ��ȣ�ɸ� ���ϰ� �Ȱɸ� ���� �������� ó��
				- ������ �޺κ��� �߷��� �ջ�� ���ϵ� ������ �κб����� ������ Ǯ���� ����
				- unalz 0.31
	2005/01/08	- ��ȣ �߸� �Է½� �ѹ� üũ�� �ι�°�� ���� ��ȣ�� �Է��ص� Ǯ�� ���ϰ� �Ǵ� ���� ����
	2005/02/05	- ���� ������ deflate �� ���� CRC Ȯ�� ��� �߰�
	2005/03/07	- bzip2, raw ���Ͽ� ���� ���� CRC Ȯ�� ��� �߰�
	2005/03/13	- ALZ ������ �ƴҰ�� ���� �ڵ�(ERR_NOT_ALZ_FILE) �߰�
	2005/06/16	- GetFileList() �Լ� ���� ����(����Ÿ�� ����)
	2005/06/18	- by goweol : utf-8 ���� �����̸����� ���� �����÷ο� �߻��ϴ� ���� ����
				- unalz 0.4
	2005/06/22	- by goweol : -l �ɼ����� ���� ������ ��� �߰�
				- UnAlzUtils.cpp/h ������ ������Ʈ�� �߰� 
	2005/06/29	- by xxfree86 : MacOSX 10.4.1  gcc 4.0 ���� iconv ���� ������ ���� ����
				- �򿣵�ȿ��� CRC üũ�� ���� �߻��ϴ� ������ ����(?)
	2005/07/02	- unalz Ŀ�ǵ� ���� ��� ����, ����Ǯ ��� ���� ���� ��� �߰�..
				- ���� ������ ���Ͻð��� ���� �ð����� �����ϴ� �ڵ� �߰� - from unalz_wcx_01i.zip
	2005/07/09	- unalz 0.5
	2005/07/24	- -d �� ��� ��θ� "/" �� ���۵Ǵ� �����η� �����ϸ� ���α׷��� �״� ���� ����(Pavel Roskin)
				- pipemode �߰� - �޽������� �������� ����Ѵ�(Pavel Roskin)
				- ����Ʈ ��忡�� ���� ����/�ð��� �ý��� ������ ������ ���� ǥ��(Pavel Roskin)
				- Ŀ�ǵ���ο��� -pwd �ɼ����� ��ȣ ������� �߰�
				- unalz 0.51
	2005/07/27	- main() �� setlocale() �߰�
				- unalz 0.52
	2005/10/15	- NetBSD ���� ������ �ǵ��� ���� (by minskim@bawi)
	2005/11/21	- buffer overflow ���� ���� (by Ulf Harnhammar)
				- unalz 0.53
	2006/03/10	- .. ���� ���� ���� ���� ���� (by vuln@secunia)
				- unalz 0.55
	2006/04/23	- ����� ó���� ��Ÿ�ӿ� �ϵ��� ����
	2006/12/31	- strcpy/strcat/sprintf �� ���� ���� �����÷ο� ���ɼ��� �ִ� �Լ� ���� (by liam.joo@gmail)
				- unalz 0.60
	2007/02/10	- ��������� strlcpy, strlcat ������ ���� ����
				- unalz 0.61
	2007/04/12	- unalz command ���� ������ ��ȣ �Է½� \n�� ���ԵǴ� ���� ����
	2008/04/04	- debian ���� ���� ���� (by cwryu@debian )
				- �ҽ� ����, NULL iterator ���� ����
				- unalz 0.62
	2009/01/09  - apple gcc ������ ���� ����(by lacovnk)
				- unalz 0.63
	2009/01/20  - 2GB�� �Ѵ� ���� ó�� ���� ����(by bsjeon@hanmail)
				- ���� ������ �κ� ����
				- �ҽ� ����
				- unalz 0.64
	2009/04/01  - bzip2 1.0.5 update
				- vs2008 ������ ���� �̽� ����(atlconv �Ⱦ���, crt secure warning disable) (by kaisyu@gmail)
				- unalz 0.65

  
  ��� :
	- alz ������ ���� ���� (deflate/���� bzip2/raw)
	- ���� ���� ���� ���� (alz, a00, a01.. )
	- �پ��� �÷��� ���� (Win32/POSIX(BSD/LINUX/DARWIN))
	- ��ȣ�ɸ� ������ ���� ����
	- �޺κ��� �߸� ���ϵ� ������ �κб��� ���� ���� ����
	- CRC üũ���


  ������ �ɼ� (-DXXXX)
	- _WIN32 : WIN32 
	- _UNALZ_ICONV : iconv �� ����ؼ� code ������ ��ȯ ����
	- _UNALZ_UTF8 : _UNALZ_ICONV �� ����� ��� �⺻ �ڵ��������� "UTF-8" �� ����

*/


#ifndef _UNALZ_H_
#define _UNALZ_H_

#include <cstdlib>
#include <cstring>
#include <vector>
using namespace std;


#ifndef INT64
#ifdef _WIN32
#	define INT64 __int64
#else
#	define INT64 long long
#endif
#endif

#ifndef UINT64
#ifdef _WIN32
#	define UINT64 unsigned __int64
#else
#	define UINT64 unsigned long long
#endif
#endif

#ifndef UINT32
	typedef unsigned int		UINT32;
#endif

#ifndef UINT16
	typedef unsigned short		UINT16;
#endif

#ifndef SHORT
	typedef short SHORT;
#endif
#ifndef BYTE
	typedef unsigned char       BYTE;
#endif
#ifndef CHAR
	typedef char CHAR;
#endif
#ifndef BYTE
	typedef unsigned char BYTE;
#endif
#ifndef UINT
	typedef unsigned int UINT;
#endif
#ifndef LONG
	typedef long LONG;
#endif
#ifndef BOOL
#	ifndef BOOL_DEFINED		// �̹� BOOL �� DEFINE �Ǿ� ������ BOOL_DEFINED �� define �ؼ� ������ ������ ���� �� �ִ�.
	typedef int BOOL;
#	endif
#endif
#ifndef FALSE
#	define FALSE               0
#endif
#ifndef TRUE
#	define TRUE                1
#endif
#ifndef HANDLE
#	ifdef _WIN32
	typedef void *HANDLE;
#	else
	typedef FILE *HANDLE;
#	endif
#endif
#ifndef ASSERT
#	include <assert.h>
//#	define ASSERT(x) assert(x)
#	define ASSERT(x) {printf("unalz assert at file:%s line:%d\n", __FILE__, __LINE__);}
#endif




namespace UNALZ
{

#ifdef _WIN32
#	pragma pack(push, UNALZ, 1)			// structure packing 
#else
#	pragma pack(1)
#endif

static const char UNALZ_VERSION[]   = "CUnAlz0.65";
static const char UNALZ_COPYRIGHT[] = "Copyright(C) 2004-2009 by kippler@gmail.com ( http://www.kipple.pe.kr ) ";

enum		{ALZ_ENCR_HEADER_LEN=12}; // xf86
// �� ���� ��..
struct SAlzHeader
{
	UINT32	unknown;			// ??
};

/*
union _UGeneralPurposeBitFlag			// zip ������ ���..
{
	SHORT	data;
	struct 
	{
		BYTE bit0 : 1;
		BYTE bit1 : 1;
		BYTE bit2 : 1;
		BYTE bit3 : 1;
		BYTE bit4 : 1;
		BYTE bit5 : 1;
	};
};
*/

enum COMPRESSION_METHOD					///<  ���� ���..
{
	COMP_NOCOMP = 0,
	COMP_BZIP2 = 1,
	COMP_DEFLATE = 2,
	COMP_UNKNOWN = 3,					// unknown!
};

enum ALZ_FILE_ATTRIBUTE
{
	ALZ_FILEATTR_READONLY	= 0x1,
	ALZ_FILEATTR_HIDDEN		= 0x2,
	ALZ_FILEATTR_DIRECTORY	= 0x10,
	ALZ_FILEATTR_FILE		= 0x20,			
};

enum ALZ_FILE_DESCRIPTOR
{
	ALZ_FILE_DESCRIPTOR_ENCRYPTED			= 0x01,		// ��ȣ �ɸ� ����
	ALZ_FILE_DESCRIPTOR_FILESIZEFIELD_1BYTE = 0x10,		// ���� ũ�� �ʵ��� ũ��
	ALZ_FILE_DESCRIPTOR_FILESIZEFIELD_2BYTE = 0x20,
	ALZ_FILE_DESCRIPTOR_FILESIZEFIELD_4BYTE = 0x40,
	ALZ_FILE_DESCRIPTOR_FILESIZEFIELD_8BYTE = 0x80,
};

struct _SAlzLocalFileHeaderHead			///<  ���� ���.
{
	SHORT	fileNameLength;
	BYTE    fileAttribute;			    // from http://www.zap.pe.kr, enum FILE_ATTRIBUE ����
	UINT32  fileTimeDate;				// dos file time
	
	BYTE	fileDescriptor;				///<  ���� ũ�� �ʵ��� ũ�� : 0x10, 0x20, 0x40, 0x80 ���� 1byte, 2byte, 4byte, 8byte.
										///<  fileDescriptor & 1 == ��ȣ�ɷȴ��� ����
	BYTE	unknown2[1];				///<  ???

	/*
	SHORT	versionNeededToExtract;
	_UGeneralPurposeBitFlag	generalPurposeBitFlag;
	SHORT	compressionMethod;
	SHORT	lastModFileTime;
	SHORT	lastModFileDate;
	UINT32	crc32;
	UINT32	compressedSize;
	UINT32	uncompressedSize;
	SHORT	fileNameLength;
	SHORT	extraFieldLength;
	*/
};

struct SAlzLocalFileHeader
{
	SAlzLocalFileHeader() { memset(this, 0, sizeof(*this)); }
	//~SAlzLocalFileHeader() { if(fileName) free(fileName); if(extraField) free(extraField); }
	void Clear() { if(fileName) free(fileName); fileName=NULL; if(extraField) free(extraField);extraField=NULL; }
	_SAlzLocalFileHeaderHead	head;

	BYTE					compressionMethod;			///< ���� ��� : 2 - deflate, 1 - ���� bzip2, 0 - ���� ����.
	BYTE					unknown;
	UINT32					fileCRC;					///< ������ CRC, �ֻ��� ����Ʈ�� ��ȣ üũ�����ε� ���ȴ�.

	INT64					compressedSize;
	INT64					uncompressedSize;

	CHAR*					fileName;
	BYTE*					extraField;
	INT64					dwFileDataPos;				///<  file data �� ����� ��ġ..
	
	BYTE					encChk[ALZ_ENCR_HEADER_LEN];	// xf86
};

struct _SAlzCentralDirectoryStructureHead
{
	UINT32	dwUnknown;						///<  �׻� NULL �̴���..
	UINT32	dwUnknown2;						///<  �Ƹ��� crc
	UINT32	dwCLZ03;						///<  "CLZ0x03" - 0x035a4c43 ���� ǥ���ϴµ�.
	/*
	SHORT	versionMadeBy;
	SHORT	versionNeededToExtract;
	_UGeneralPurposeBitFlag	generalPurposeBitFlag;
	SHORT	compressionMethod;
	SHORT	lastModFileTime;
	SHORT	lastModFileDate;
	UINT32	crc32;
	UINT32	compressedSize;
	UINT32	uncompressedSize;
	SHORT	fileNameLength;
	SHORT	extraFieldLength;
	SHORT	fileCommentLength;
	SHORT	diskNumberStart;
	SHORT	internalFileAttributes;
	UINT32	externalFileAttributes;
	UINT32	relativeOffsetOfLocalHeader;
	*/
};

struct SCentralDirectoryStructure
{
	SCentralDirectoryStructure() { memset(this, 0, sizeof(*this)); }
	//~SCentralDirectoryStructure() { if(fileName) free(fileName); if(extraField) free(extraField);if(fileComment)free(fileComment); }
	_SAlzCentralDirectoryStructureHead	head;
	/*
	CHAR*	fileName;
	BYTE*	extraField;
	CHAR*	fileComment;
	*/
};


/*
struct _SEndOfCentralDirectoryRecordHead
{
	SHORT	numberOfThisDisk;
	SHORT	numberOfTheDiskWithTheStartOfTheCentralDirectory;
	SHORT	centralDirectoryOnThisDisk;
	SHORT	totalNumberOfEntriesInTheCentralDirectoryOnThisDisk;
	UINT32	sizeOfTheCentralDirectory;
	UINT32	offsetOfStartOfCentralDirectoryWithREspectoTotheStartingDiskNumber;
	SHORT	zipFileCommentLength;
};
*/

/*
struct SEndOfCentralDirectoryRecord
{
	SEndOfCentralDirectoryRecord() { memset(this, 0, sizeof(*this)); }
	~SEndOfCentralDirectoryRecord() { if(fileComment) free(fileComment); }
	_SEndOfCentralDirectoryRecordHead head;
	CHAR*	fileComment;
};
*/

#ifdef _WIN32
#	pragma pack(pop, UNALZ)		///<  PACKING ���� ����
#else
#	pragma pack()				// restore packing
#endif



///<  PROGRESS CALLBACK FUNCTION - ���� ���� ���� ��Ȳ�� �˰� ������ �̰� ���� �ȴ�.
typedef void (_UnAlzCallback)(const char* szFileName, INT64 nCurrent, INT64 nRange, void* param, BOOL* bHalt);


class CUnAlz  
{
public:
	CUnAlz();
	~CUnAlz();
	BOOL	Open(const char* szPathName);
	void	Close();
	BOOL	SetCurrentFile(const char* szFileName);
	BOOL	ExtractCurrentFile(const char* szDestPathName, const char* szDestFileName=NULL);
	BOOL	ExtractCurrentFileToBuf(BYTE* pDestBuf, int nBufSize);		// pDestBuf=NULL �� ��� �׽�Ʈ�� �����Ѵ�.
	BOOL	ExtractAll(const char* szDestPathName);
	void	SetCallback(_UnAlzCallback* pFunc, void* param=NULL);
	void	SetPipeMode(BOOL bPipeMode) {m_bPipeMode=bPipeMode;}

	void	SetPassword(char *passwd);  // xf86
	BOOL	chkValidPassword();			// xf86
	BOOL	IsEncrypted() { return m_bIsEncrypted; };

#ifdef _UNALZ_ICONV
	void	SetDestCodepage(const char* szToCodepage);
#endif

public :			///<  WIN32 ���� ( UNICODE ó���� )

#ifdef _WIN32
#ifndef __GNUWIN32__
#ifndef LPCWSTR
	typedef const wchar_t* LPCWSTR;
#endif
	BOOL	Open(LPCWSTR szPathName);
	BOOL	SetCurrentFile(LPCWSTR szFileName);
	static BOOL		IsFolder(LPCWSTR szPathName);
#endif // __GNUWIN32__	
#endif // _WIN32

public :
	typedef vector<SAlzLocalFileHeader>		FileList;					///<  ���� ���.
	FileList*			GetFileList() { return &m_fileList; };			///<  file ��� ����
	void				SetCurrentFile(FileList::iterator newPos);		///< low level ����..
	FileList::iterator	GetCurFileHeader() { return m_posCur; };		///<  ���� (SetCurrentFile() �� ���õ�) ���� ����

public :
	enum ERR							///< ���� �ڵ� - ���� �ʿ�..
	{
		ERR_NOERR,
		ERR_GENERAL,					///< GENERAL ERROR
		ERR_CANT_OPEN_FILE,				///< �ҽ� ���� ���� ����
		ERR_CANT_OPEN_DEST_FILE,		///< ��� ���� ���� ����
//		ERR_CANT_CREATE_DEST_PATH,		///< ��� ��� ����� ����
		ERR_CORRUPTED_FILE,				///< ���� ����?
		ERR_NOT_ALZ_FILE,				///< ALZ ������ �ƴϴ�.
		ERR_CANT_READ_SIG,				///< signature �б� ����
		ERR_CANT_READ_FILE,

		ERR_AT_READ_HEADER,
		ERR_INVALID_FILENAME_LENGTH,
		ERR_INVALID_EXTRAFIELD_LENGTH,
		ERR_CANT_READ_CENTRAL_DIRECTORY_STRUCTURE_HEAD, 
		ERR_INVALID_FILENAME_SIZE,
		ERR_INVALID_EXTRAFIELD_SIZE,
		ERR_INVALID_FILECOMMENT_SIZE,
		ERR_CANT_READ_HEADER,
		ERR_MEM_ALLOC_FAILED,
		ERR_FILE_READ_ERROR,
		ERR_INFLATE_FAILED,
		ERR_BZIP2_FAILED,
		ERR_INVALID_FILE_CRC,	
		ERR_UNKNOWN_COMPRESSION_METHOD,

		ERR_ICONV_CANT_OPEN,
		ERR_ICONV_INVALID_MULTISEQUENCE_OF_CHARACTERS,
		ERR_ICONV_INCOMPLETE_MULTIBYTE_SEQUENCE,
		ERR_ICONV_NOT_ENOUGH_SPACE_OF_BUFFER_TO_CONVERT,
		ERR_ICONV_ETC,

		ERR_PASSWD_NOT_SET,
		ERR_INVALID_PASSWD,
		ERR_USER_ABORTED,

	};
	ERR		GetLastErr(){return m_nErr;}
	const char* GetLastErrStr(){return LastErrToStr(m_nErr);}
	const char* LastErrToStr(ERR nERR);

	enum SIGNATURE							///<  zip file signature - little endian
	{
		SIG_ERROR							= 0x00,
		SIG_EOF								= 0x01,
		SIG_ALZ_FILE_HEADER					= 0x015a4c41,	///<  ALZ 0x01
		SIG_LOCAL_FILE_HEADER				= 0x015a4c42,	///<  BLZ 0x01
		SIG_CENTRAL_DIRECTORY_STRUCTURE		= 0x015a4c43,	///<  CLZ 0x01
		SIG_ENDOF_CENTRAL_DIRECTORY_RECORD	= 0x025a4c43,	///<  CLZ 0x02
	};

public :
	static BOOL			DigPath(const CHAR* szPathName);
	static BOOL			IsFolder(const CHAR* szPathName);
	static const char*	GetVersion() { return UNALZ_VERSION; }
	static const char*	GetCopyright() { return UNALZ_COPYRIGHT; }
	BOOL				IsHalted() { return m_bHalt; }		// by xf86

public :
	static void			safe_strcpy(char* dst, const char* src, size_t dst_size);
	static void			safe_strcat(char* dst, const char* src, size_t dst_size);
	static unsigned int _strlcpy (char *dest, const char *src, unsigned int size);
	static unsigned int _strlcat (char *dest, const char *src, unsigned int size);

private :
	SIGNATURE	ReadSignature();
	BOOL		ReadAlzFileHeader();
	BOOL		ReadLocalFileheader();
	BOOL		ReadCentralDirectoryStructure();
	BOOL		ReadEndofCentralDirectoryRecord();

private :
	enum EXTRACT_TYPE				///<   ���� ���� Ÿ��.
	{
		ET_FILE,					///<  FILE*
		ET_MEM,						///<  memory buffer
	};
	struct		SExtractDest		///<  ���� ���� ���.
	{
		SExtractDest() { memset(this, 0, sizeof(SExtractDest)); }
		EXTRACT_TYPE nType;			///<  ����� �����ΰ�  �޸� �ΰ�..
		FILE*		fp;				///<  ET_FILE �� ��� ��� FILE*
		BYTE*		buf;			///<  ET_MEM �� ��� ��� ������
		UINT32		bufsize;		///<  ET_MEM �� ��� ��� ������ ũ��
		UINT32		bufpos;			///<  ET_MEM �� ��� ��� ���ۿ� ���� �ִ� ��ġ
	};
	int			WriteToDest(SExtractDest* dest, BYTE* buf, int nSize);

private :
	BOOL		ExtractTo(SExtractDest* dest);

	//BOOL		ExtractDeflate(FILE* fp, SAlzLocalFileHeader& file);
	//BOOL		ExtractBzip2_bak(FILE* fp, SAlzLocalFileHeader& file); - ������(�߸���) ��� 
	BOOL		ExtractDeflate2(SExtractDest* dest, SAlzLocalFileHeader& file);
	BOOL		ExtractBzip2(SExtractDest* dest, SAlzLocalFileHeader& file);
	BOOL		ExtractRawfile(SExtractDest* dest, SAlzLocalFileHeader& file);

private :		// bzip2 ���� ó�� �Լ�..
	typedef void MYBZFILE;
	MYBZFILE*	BZ2_bzReadOpen(int* bzerror, CUnAlz* f, int verbosity, int _small, void* unused, int nUnused);
	int			BZ2_bzread(MYBZFILE* b, void* buf, int len );
	int			BZ2_bzRead(int* bzerror, MYBZFILE* b, void* buf, int len);
	void		BZ2_bzReadClose( int *bzerror, MYBZFILE *b );

private :		// ���� ���� ���� ó���� ���� ����(lapper^^?) Ŭ����
	BOOL		FOpen(const char* szPathName);
	void		FClose();
	INT64		FTell();
	BOOL		FEof();
	BOOL		FSeek(INT64 offset);
	BOOL		FRead(void* buffer, UINT32 nBytesToRead, int* pTotRead=NULL);

	BOOL		IsDataDescr() { return m_bIsDataDescr; };   // xf86
	int			getPasswordLen() { return strlen(m_szPasswd); };

	enum		{MAX_FILES=1000};								///< ó�� ������ ���� ���� ���� ��.
	enum		{MULTIVOL_TAIL_SIZE=16,MULTIVOL_HEAD_SIZE=8};	///< ���� ����� �ö���, ��� ũ�� 
	struct SFile												///< ���� ���� ����
	{
		HANDLE	fp;
		INT64	nFileSize;
		int		nMultivolHeaderSize;
		int		nMultivolTailSize;
	};

	SFile		m_files[MAX_FILES];					///< ���� ���� ���� array - �����Ѱ�?
	int			m_nCurFile;							///< m_files ���� ���� ó������ ������ ��ġ.
	int			m_nFileCount;						///< ���� ���� ����..
	INT64		m_nVirtualFilePos;					///< ��Ƽ���������� ������ ��ġ
	INT64		m_nCurFilePos;						///< ���� ������ ������ ��ġ.
	BOOL		m_bIsEOF;							///< ������ ������ (���� ���� �����ؼ�) �Գ�?

	BOOL		m_bIsEncrypted;						///< by xf86
	BOOL		m_bIsDataDescr;
#define UNALZ_LEN_PASSWORD	512
	char		m_szPasswd[UNALZ_LEN_PASSWORD];
	BOOL		m_bPipeMode;						///< pipemode - �޽��� ��¾��� stdout ���θ� ���

private :
	/*			from CZipArchive
	void		CryptDecodeBuffer(UINT32 uCount, CHAR *buf);
	void		CryptInitKeys();
	void		CryptUpdateKeys(CHAR c);
	BOOL		CryptCheck(CHAR *buf);
	CHAR		CryptDecryptCHAR();
	void		CryptDecode(CHAR &c);
	UINT32		CryptCRC32(UINT32 l, CHAR c);
	*/

private :		// encryption ó��
	BOOL		IsEncryptedFile(BYTE fileDescriptor);
	BOOL		IsEncryptedFile();
	void		InitCryptKeys(const CHAR* szPassword);
	void		UpdateKeys(BYTE c);
	BOOL		CryptCheck(const BYTE* buf);
	BYTE		DecryptByte();
	void		DecryptingData(int nSize, BYTE* data);
	UINT32		CRC32(UINT32 l, BYTE c);
	UINT32		m_key[3];

private :
	FileList			m_fileList;					///< �������� ���� ���� ���
	ERR					m_nErr;
	FileList::iterator	m_posCur;					///< ���� ����
	_UnAlzCallback*		m_pFuncCallBack;
	void*				m_pCallbackParam;
	BOOL				m_bHalt;

#ifdef _UNALZ_ICONV

#define UNALZ_LEN_CODEPAGE	256
	char				m_szToCodepage[UNALZ_LEN_CODEPAGE];		///< codepage 
	char				m_szFromCodepage[UNALZ_LEN_CODEPAGE];		///< "CP949"
#endif
};
}

using namespace UNALZ;

#endif
