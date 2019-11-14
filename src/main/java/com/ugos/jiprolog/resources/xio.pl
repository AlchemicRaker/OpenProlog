/*************************
 * IO library
 *
 * Copyright (C) 1999-2004 Ugo Chirico
 *
 * This is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 **************************/

:-	module(jipxio, [
	see/1, see/2, seen/0, seen/1, seeing/1, seeing/2, read/1, read/2, read_term/2, read_term/3, read_clause/1, read_clause/2, get0/1,
	get0/2, get/1, get/2, get_byte/1, get_byte/2, get_code/1, get_code/2, get_char/1, get_char/2, peek_byte/1, peek_byte/2, peek_code/1,
	peek_code/2, peek_char/1, peek_char/2, tell/1, tell/2, told/0, told/1, telling/1, telling/2, write/2, writeq/1,
	writeq/2, writeln/1, writeln/2, write_canonical/2, write_term/2, write_term/3, put/1, put/2, put_byte/1, put_byte/2, put_code/1,
	put_code/2, put_char/1, skip/1, skip/2, put_char/2, nl/1, flush_output/0, flush_output/1, open/3, open/4, close/1,
	print/1, print/2, display/1, display/2, close/2, at_end_of_stream/0, at_end_of_stream/1, stream_property/2,
	access_file/2, exists_file/1, exists_directory/1, same_file/2, working_directory/2, searchpath/1, absolute_file_name/2,
	is_absolute_file_name/1, file_attributes/7, file_name_extension/2, size_file/2, time_file/2, file_directory_name/2,
	file_base_name/2, delete_file/1, delete_directory/1, rename_file/2, dir/0, dir/1, make_directory/1, current_stream/3, cd/1,
	current_output/1, current_input/1, current_stream/1, set_output/1, set_input/1, seek/4, set_stream_position/2, set_stream/2,
	stream_position_data/3,directory_files/2,
	format/2, format/3
]).

:- '$custom_built_in'([
	see/1, see/2, seen/0, seen/1, seeing/1, seeing/2, read/1, read/2, read_term/2, read_term/3, read_clause/1, read_clause/2, get0/1,
	get0/2, get/1, get/2, get_byte/1, get_byte/2, get_code/1, get_code/2, get_char/1, get_char/2, peek_byte/1, peek_byte/2, peek_code/1,
	peek_code/2, peek_char/1, peek_char/2, tell/1, tell/2, told/0, told/1, telling/1, telling/2, write/2, writeq/1,
	writeq/2, writeln/1, writeln/2, write_canonical/2, write_term/2, write_term/3, put/1, put/2, put_byte/1, put_byte/2, put_code/1,
	put_code/2, put_char/1, skip/1, skip/2, put_char/2, nl/1, flush_output/0, flush_output/1, open/3, open/4, close/1,
	print/1, print/2, display/1, display/2, close/2, at_end_of_stream/0, at_end_of_stream/1, stream_property/2,
	access_file/2, exists_file/1, exists_directory/1, same_file/2, working_directory/2, searchpath/1, absolute_file_name/2,
	is_absolute_file_name/1, file_attributes/7, file_name_extension/2, size_file/2, time_file/2, file_directory_name/2,
	file_base_name/2, delete_file/1, delete_directory/1, rename_file/2, dir/0, dir/1, make_directory/1, current_stream/3, cd/1,
	current_output/1, current_input/1, current_stream/1, set_output/1, set_input/1, seek/4, set_stream_position/2, set_stream/2,
	stream_position_data/3,directory_files/2,
	format/2, format/3
]).

:- assert(ver(jipxio, '4.0.4')).

:- op(400, fx, cd).

:- extern(see/2, 'com.ugos.jiprolog.extensions.io.See2').
:- extern('$read'/2, 'com.ugos.jiprolog.extensions.io.Read2').
:- extern('$read_term'/3, 'com.ugos.jiprolog.extensions.io.ReadTerm3').
:- extern('$get0'/2, 'com.ugos.jiprolog.extensions.io.Get02').
:- extern('$get_byte'/2, 'com.ugos.jiprolog.extensions.io.GetByte2').
:- extern('$get_code'/2, 'com.ugos.jiprolog.extensions.io.GetCode2').
:- extern('$get_char'/2, 'com.ugos.jiprolog.extensions.io.GetChar2').
:- extern('$peek_byte'/2, 'com.ugos.jiprolog.extensions.io.PeekByte2').
:- extern('$peek_code'/2, 'com.ugos.jiprolog.extensions.io.PeekCode2').
:- extern('$peek_char'/2, 'com.ugos.jiprolog.extensions.io.PeekChar2').
:- extern('$seen'/1, 'com.ugos.jiprolog.extensions.io.Seen1').
:- extern(tell/2, 'com.ugos.jiprolog.extensions.io.Tell2').
:- extern('$append'/2, 'com.ugos.jiprolog.extensions.io.Append2').
:- extern('$write'/2, 'com.ugos.jiprolog.extensions.io.Write2').
:- extern('$writeq'/2, 'com.ugos.jiprolog.extensions.io.Writeq2').
:- extern('$write_canonical'/2, 'com.ugos.jiprolog.extensions.io.WriteCanonical2').
:- extern('$put'/2, 'com.ugos.jiprolog.extensions.io.Put2').
:- extern('$put_byte'/2, 'com.ugos.jiprolog.extensions.io.PutByte2').
:- extern('$put_code'/2, 'com.ugos.jiprolog.extensions.io.PutCode2').
:- extern('$put_char'/2, 'com.ugos.jiprolog.extensions.io.PutChar2').
:- extern('$nl'/1, 'com.ugos.jiprolog.extensions.io.Nl1').
:- extern('$flush_output'/1, 'com.ugos.jiprolog.extensions.io.FlushOutput1').
:- extern('$told'/1, 'com.ugos.jiprolog.extensions.io.Told1').
:- extern('$current_output'/1, 'com.ugos.jiprolog.extensions.io.CurrentOutput1').
:- extern('$current_input'/1, 'com.ugos.jiprolog.extensions.io.CurrentInput1').
:- extern(current_stream/1, 'com.ugos.jiprolog.extensions.io.CurrentStream1').
:- extern('$set_output'/1, 'com.ugos.jiprolog.extensions.io.SetOutput1').
:- extern('$set_input'/1, 'com.ugos.jiprolog.extensions.io.SetInput1').
:- extern('$access_file'/2, 'com.ugos.jiprolog.extensions.io.AccessFile2').
:- extern(absolute_file_name/2, 'com.ugos.jiprolog.extensions.io.AbsoluteFileName2').
:- extern(file_attributes/7, 'com.ugos.jiprolog.extensions.io.FileAttributes7').
:- extern('$stream_position'/4, 'com.ugos.jiprolog.extensions.io.StreamPosition4').
:- extern('$stream_property'/3, 'com.ugos.jiprolog.extensions.io.StreamProperty3').
:- extern('$char_atom'/2, 'com.ugos.jiprolog.extensions.io.CharAtom2').


open(File, Mode, _, Options):-
	(	var(File)
	;	var(Mode)
	;	\+ ground(Options)
	),
	error(instantiation_error).

open(_, Mode, _, _):-
	\+ atom(Mode),
	error(type_error(atom,Mode)).

open(_, _, _, Options):-
	\+ is_list(Options),
	error(type_error(list,Options)).

open(_, _, Handle, _):-
	nonvar(Handle),
	error(uninstantiation_error(Handle)).

open(File, _, _, _):-
	\+ atom(File),
	error(domain_error(source_sink,File)).

open(_, Mode, _, _):-
	Mode \== read,
	Mode \== write,
	Mode \== append,
	error(domain_error(io_mode,Mode)).

open(_, _, _, Options):-
	valid_open_options(Options),
	member(alias(Alias), Options),
	stream_property(_, alias(Alias)),
	error(permission_error(open,source_sink,alias(Alias))).

open(File, Mode, Handle, Options):-
    jipxio:'$open'(Mode, File, Handle),
    set_stream_properties(Handle, Options).


open(File, Mode, Handle):-
	open(File, Mode, Handle, []).

valid_open_options([Option| Options]) :-
	(	var(Option) ->
		error(instantiation_error)
	;	jipxio:valid_open_option(Option) ->
		jipxio:valid_open_options(Options)
	;	error(domain_error(stream_option,Option))
	).

valid_open_options([]).

valid_open_option(eof_action(Action)) :-
	(	Action == error
	;	Action == eof_code
	;	Action == reset
	).
valid_open_option(alias(Alias)) :-
	atom(Alias).
valid_open_option(type(Type)) :-
	(	Type == text
	;	Type == binary
	).
valid_open_option(reposition(Reposition)) :-
	(	Reposition == true
	;	Reposition == false
	).
valid_open_option(encoding(Encoding)) :-
	(	Encoding == 'UTF-8'
	;	Encoding == 'US-ASCII'
	).

'$open'(write, File, Handle):-
    tell(File, Handle).

'$open'(read, File, Handle):-
    see(File, Handle).

'$open'(append, File, Handle):-
    append(File, Handle).


current_stream(FileName, Mode, Handle):-
	nonvar(Handle),
	!,
	check_handle(Handle, Handle1),
    stream_property(Handle, file_name(FileName)),
    stream_property(Handle, mode(Mode)).

current_stream(FileName, Mode, Handle):-
    stream_property(Handle, file_name(FileName)),
    stream_property(Handle, mode(Mode)).

/**********************************
* Read Predicates
***********************************/

see(Handle):-
	check_handle(Handle, Handle1),
    set_input(Handle),
    !.

see(File):-
    see(File, Handle),
    set_input(Handle).



%see(File, Handle):-
%   xcall('com.ugos.jiprolog.extensions.io.See2', [File, Handle]).

seeing(Handle):-
	check_handle(Handle, Handle1),
	current_input(Handle),
	!.

seeing(File):-
    current_input(Handle),
    seeing(Handle, File).

seeing(Handle, File):-
    check_handle(Handle, Handle1),
    stream_property(Handle, file_name(File)).

read(Term):-
    current_input(Handle),
    read(Handle, Term).



read(Handle, Term):-
	(	var(Handle) ->
		error(instantiation_error)
	;	check_handle(Handle, Handle1)
	),
	(	\+ current_stream(Handle) ->
		error(existence_error(stream,Handle))
	;	\+ stream_property(Handle, mode(read)) ->
		error(permission_error(input,stream,Handle))
	;	stream_property(Handle, type(binary)) ->
		error(permission_error(input,binary_stream,Handle))
	;	'$read'(Handle, Term)
	).

read_term(Term, Options):-
	check_read_term_options(Options, Options),
    current_input(Handle),
    read_term(Handle, Term, Options),
    read_term_options(Handle, Term, Options).



read_term(Handle, Term, Options):-
	check_read_term_options(Options, Options),
	check_handle(Handle, Handle1),
	'$read_term'(Handle, Term, Options),
    read_term_options(Handle, Term, Options).

read_clause(Term, Options):-
	check_read_term_options(Options, Options),
    current_input(Handle),
    read_term(Handle, Term, Options),
    read_term_options(Handle, Term, Options).

read_clause(Handle, Term, Options):-
	check_read_term_options(Options, Options),
	check_handle(Handle, Handle1),
	'$read_term'(Handle, Term, Options),
    read_term_options(Handle, Term, Options).


get0(Code):-
    current_input(Handle),
    get0(Handle, Code).

get0(Handle, Code):-
    check_handle(Handle, Handle1),
    '$get0'(Handle, Code).

get(Code):-
    current_input(Handle),
    get(Handle, Code).

get(Handle, Code):-
    get0(Handle, Code),
    Code >= 32.

get_byte(Byte):-
    current_input(Handle),
    '$get_byte'(Handle, Byte).

get_byte(Handle, Byte):-
	check_handle(Handle, Handle1),
	'$get_byte'(Handle, Byte).

get_code(Code) :-
    current_input(Handle),
	'$get_code'(Handle, Code).

get_code(Handle, Code) :-
	check_handle(Handle, Handle1),
	'$get_code'(Handle, Code).

get_char(Char):-
    current_input(Handle),
	'$get_char'(Handle, Char).

get_char(Handle, Char):-
	check_handle(Handle, Handle1),
	'$get_char'(Handle, Char).

%	xcall('com.ugos.jiprolog.extensions.io.GetChar2', [Handle, Code]),
%	( 	'$char'(Code, Char0) ->
%  		Char0 = Char0
%	;	error(representation_error(character))
% 	).


'$char'(-1, end_of_file) :-
	!.

'$char'(C, A) :-
	C > 31,
	!,
	'$char_atom'(C, A).

'$char'(_, _) :-
	error(representation_error(character)).

peek_byte(Byte) :-
    current_input(Handle),
    '$peek_byte'(Handle, Byte).

peek_byte(Handle, Byte):-
	check_handle(Handle, Handle1),
	'$peek_byte'(Handle, Byte).

peek_code(Code) :-
    current_input(Handle),
    '$peek_code'(Handle, Code).

peek_code(Handle, Code):-
	check_handle(Handle, Handle1),
    '$peek_code'(Handle, Code).

peek_char(Char) :-
    current_input(Handle),
    '$peek_char'(Handle, Char).

peek_char(Handle, Char) :-
	check_handle(Handle, Handle1),
	'$peek_char'(Handle, Char).
%	;	xcall('com.ugos.jiprolog.extensions.io.PeekByte2', [Handle, Byte])
%	),
%    ( 	'$char'(Byte, Char0) ->
%  		Char = Char0
% 	; 	error(representation_error(character))
% 	).


skip(Char):-
    get0(C),
    (C = Char ; C = -1).

skip(Char):-
    skip(Char).

skip(Handle, Char):-
    get0(Handle, C),
    (C = Char ; C = -1),
    !.

skip(Handle, Char):-
    skip(Char).

seen:-
    current_input(Handle),
    seen(Handle),
    set_input(user_input).

seen(Handle):-
    check_handle(Handle, Handle1),
    '$seen'(Handle).

/**********************************
* Write Predicates
***********************************/

tell(Handle):-
    check_handle(Handle, Handle1),
    set_output(Handle).

tell(File):-
    tell(File, Handle),
    set_output(Handle).

%tell(File, Handle):-
%   xcall('com.ugos.jiprolog.extensions.io.Tell2', [File, Handle]).

append(Handle):-
    check_handle(Handle, Handle1),
	set_output(Handle),
	!.

append(File):-
    append(File, Handle),
    set_output(Handle).

append(File, Handle):-
	'$append'(File, Handle).

telling(Handle):-
    check_handle(Handle, Handle1),
    current_output(Handle),
    !.

telling(File):-
    current_output(Handle),
    telling(Handle, file).

telling(Handle, File):-
    check_handle(Handle, Handle1),
    stream_property(Handle, file_name(File)).


write(Handle, Term):-
	check_handle(Handle, Handle1),
	'$write'(Handle, Term).

write_canonical(Handle, Term):-
	check_handle(Handle, Handle1),
	'$write_canonical'(Handle, Term).


print(Term):-
    write(Term).

print(Handle, Term):-
    write(Handle, Term).


display(Term):-
    write_canonical(Term).

display(Handle, Term):-
    write_canonical(Handle, Term).


writeq(Term):-
    current_output(Handle),
    '$writeq'(Handle, Term).

writeq(Handle, Term):-
	check_handle(Handle, Handle1),
	'$writeq'(Handle, Term).


writeln(Term):-
    write(Term), nl.

writeln(Handle, Term):-
    write(Handle, Term), nl.


write_term(Term, Options):-
	check_write_term_options(Options, Options),
    current_output(Handle),
    write(Handle, Term),
    write_term_options(Handle, Term, Options).

write_term(Handle, Term, Options):-
	check_write_term_options(Options, Options),
    write(Handle, Term),
    write_term_options(Handle, Term, Options).


put(C):-
    current_output(Handle),
	'$put'(Handle, C).

put(Handle, C) :-
	check_handle(Handle, Handle1),
	'$put'(Handle, C).


put_byte(Byte) :-
    current_output(Handle),
	'$put_byte'(Handle, Byte).

put_byte(Handle, Byte):-
	check_handle(Handle, Handle1),
	'$put_byte'(Handle, Byte).


put_char(Char) :-
    current_output(Handle),
	'$put_char'(Handle, Char).

put_char(Handle, Char):-
	check_handle(Handle, Handle1),
	'$put_char'(Handle, Char).


put_code(Code) :-
    current_output(Handle),
	'$put_code'(Handle, Code).

put_code(Handle, Code):-
	check_handle(Handle, Handle1),
	'$put_code'(Handle, Code).


tab(N):-
    current_output(Handle),
    tab_(Handle, N).

tab(Handle, N):-
    check_handle(Handle, Handle1),
	tab_(Handle, N).

tab_(Handle, N):-
    N < 1,
    !.

tab_(Handle, N):-
    N1 is N - 1,
	'$put_code'(Handle, 32),
    tab_(Handle, N1).

nl(Handle) :-
    check_handle(Handle, Handle1),
    '$nl'(Handle).

flush_output :-
    current_output(Handle),
    '$flush_output'(Handle).

flush_output(Handle) :-
    check_handle(Handle, Handle1),
    '$flush_output'(Handle).


told :-
    current_output(Handle),
    '$told'(Handle),
    set_output(user_output).

told(Handle) :-
    check_handle(Handle, Handle1),
    '$told'(Handle).

close(Handle, Options):-
	(	var(Handle)
	;	var(Options)
	),
	error(instantiation_error).

close(_, Options):-
	member(Option, Options),
	\+ ground(Option),
	error(instantiation_error).

close(_, Options):-
	\+ is_list(Options),
	error(type_error(list,Options)).

close(_, Options):-
	member(Option, Options),
	Option \== force(true),
	Option \== force(false),
	error(domain_error(close_option,Option)).

close(Handle, _):-
    check_handle(Handle, Handle1),
	\+ current_stream(Handle),
	error(existence_error(stream,Handle)).

close(Handle, _Options):-
    close_(Handle).


close(Handle):-
	close(Handle, []).


close_(Handle):-
    check_handle(Handle, Handle1),
    stream_property(Handle, input),
    !,
    seen(Handle).

close_(Handle):-
    check_handle(Handle, Handle1),
    stream_property(Handle, output),
    !,
    told(Handle).


at_end_of_stream :-
    current_input(Handle),
	stream_property(Handle, end_of_stream(EOS)), !,
	(	EOS == at
	;	EOS == past
	),
	!.

at_end_of_stream(Handle) :-
	var(Handle),
	error(instantiation_error).

at_end_of_stream(Handle) :-
	\+ integer(Handle),
	\+ atom(Handle),
	error(domain_error(stream_or_alias,Handle)).

at_end_of_stream(Handle) :-
	check_handle(Handle, Handle1),
	(	current_stream(Handle) ->
		stream_property(Handle, end_of_stream(EOS)),
		(	EOS == at
		;	EOS == past
		), !
	;	error(existence_error(stream,Handle))
	).


current_output(Handle):-
	nonvar(Handle),
	(	\+ integer(Handle)
	;	\+ current_stream(Handle)
	),
	error(domain_error(stream,Handle)).

current_output(Handle):-
	'$current_output'(Handle).

current_input(Handle):-
	nonvar(Handle),
	(	\+ integer(Handle)
	;	\+ current_stream(Handle)
	),
	error(domain_error(stream,Handle)).

current_input(Handle):-
	'$current_input'(Handle).

set_output(Handle):-
	var(Handle),
	error(instantiation_error).

set_output(Handle):-
	\+ integer(Handle),
	\+ atom(Handle),
	error(domain_error(stream_or_alias,Handle)).


set_output(Handle):-
	check_handle(Handle, Handle1),
	(	\+ current_stream(Handle) ->
		error(existence_error(stream,Handle))
	;	\+ stream_property(Handle, mode(append)),
		\+ stream_property(Handle, mode(write)) ->
		error(permission_error(output,stream,Handle))
	;  '$set_output'(Handle)
	).


set_input(Handle):-
	var(Handle),
	error(instantiation_error).

set_input(Handle):-
	\+ integer(Handle),
	\+ atom(Handle),
	error(domain_error(stream_or_alias,Handle)).

set_input(Handle):-
	check_handle(Handle, Handle1),
	(	\+ current_stream(Handle) ->
		error(existence_error(stream,Handle))
	;	\+ stream_property(Handle, mode(read)) ->
		error(permission_error(input,stream,Handle))
	;   '$set_input'(Handle)
	).

/*******************/

access_file(File, none):-!.

access_file(File, Mode):-
	'$access_file'(File, Mode).

exists_file(File):-
	'$access_file'(File, exist).


exists_directory(File):-
    '$access_file'(File, directory).

same_file(File1, File2):-
    absolute_file_name(File1, AFile1),
    absolute_file_name(File2, AFile2),
    AFile1 == AFile2.

working_directory(X, Y):-
    chdir(X),
    chdir(Y).

searchpath(X):-
    chdir(X).

%absolute_file_name(File, Abs):-
%    xcall('com.ugos.jiprolog.extensions.io.AbsoluteFileName2', [File, Abs]).

absolute_file_name(File, Abs, _Opts):-
	absolute_file_name(File, Abs).

is_absolute_file_name(File):-
    absolute_file_name(File, File).



%file_attributes(File, Name, Ext, Dir, Abs, Size, Time):-
%    xcall('com.ugos.jiprolog.extensions.io.FileAttributes7', [File, Name, Ext, Dir, Abs, Size, Time]).

file_name_extension(File, Ext):-
    file_attributes(File, _, Ext, _, _, _, _).

size_file(File, Size):-
    exists_file(File),
    !,
    file_attributes(File, _, _, _, _, Size, _).

size_file(File, _Size):-
    error(existence_error(stream, File)).

time_file(File, Time):-
    exists_file(File),
    !,
    file_attributes(File, _, _, _, _, _, Time).

time_file(_File, _Size):-
    error(system_error(not_implemented)).

file_directory_name(File, Directory):-
    file_attributes(File, _, _, Directory, _, _, _).

file_base_name(File, Base):-
    file_attributes(File, Base, _, _, _, _, _).

delete_file(File):-
    xcall('com.ugos.jiprolog.extensions.io.DeleteFile1', [File]).

delete_directory(Dir):-
    xcall('com.ugos.jiprolog.extensions.io.DeleteFile1', [Dir]).

rename_file(File, NewFile):-
    xcall('com.ugos.jiprolog.extensions.io.RenameFile2', [File, NewFile]).

dir:-
    chdir(CurDir),
    write('File list in '),
    write(CurDir), nl,
    dir(X),
    write_dir(X).

write_dir([]):-!.

write_dir([X|Xs]):-
    write(X), nl,
    write_dir(Xs).

dir(X):-
    xcall('com.ugos.jiprolog.extensions.io.Dir1', [X]).

directory_files(Pathname, FileList):-
    xcall('com.ugos.jiprolog.extensions.io.Dir2', [Pathname, FileList]).

cd(Dir):-
    chdir(Dir).

make_directory(Dir):-
    xcall('com.ugos.jiprolog.extensions.io.MakeDirectory1', [Dir]).


% stream properties

stream_property(Handle, Prop) :-
	nonvar(Handle),
	\+ current_stream(Handle),
	error(domain_error(stream,Handle)).


stream_property(Handle, position(position(CharCount,Line,Column))) :-
	current_stream(Handle),
	'$stream_position'(Handle, CharCount, Line, Column).

stream_property(Handle, end_of_stream(EOS)) :-
	current_stream(Handle),
	(	'$stream_property'(get, Handle, eof_action(reset)) ->
		EOS = (not)
	;	'$stream_property'(get, Handle, output) ->
		EOS = (not)
	;	'$stream_property'(get, Handle, end_of_stream(at)) ->
		EOS = at
	;	'$stream_property'(get, Handle, end_of_stream(past)) ->
		EOS = past
	;	'$stream_property'(get, Handle, type(text)),
		'$peek_char'(Handle, end_of_file) ->
		EOS = at
	;	'$stream_property'(get, Handle, type(binary)),
		'$peek_byte'(Handle, -1) ->
		EOS = at
	;	EOS = (not)
	).

stream_property(Handle, Prop) :-
	current_stream(Handle),
	'$stream_property'(get, Handle, Prop),
	Prop \= end_of_stream(_).


stream_position_data(char_count, position(CharCount,_,_), CharCount).
stream_position_data(character_count, position(CharCount,_,_), CharCount).
stream_position_data(line_count, position(_,LineCount,_), LineCount).
stream_position_data(line_position, position(_,_,LinePosition), LinePosition).


set_stream_property(Handle, Prop):-
    '$stream_property'(set, Handle, Prop).

set_stream_properties(Handle, []):-!.
set_stream_properties(Handle, [Prop|Rest]):-
	'$stream_property'(set, Handle, Prop),
    set_stream_properties(Handle, Rest).


% check for aliases

check_handle(X, X).

%check_handle(Alias, Handle):-
%	atom(Alias),
%	(	Alias == user_input ->
%		Handle = -1
%	;	Alias == user_output ->
%		Handle = -2
%	;	Alias == user_error ->
%		Handle = -4
%	;	stream_property(Handle, alias(Alias))
%	),
%	!.

%check_handle(Alias, Alias):-
%   nonvar(Alias),
%   !.

%check_handle(_, _):-
%   error(instantiation_error,_).

%check_handle(input_stream, -1).



% not supported

seek(_, _, _, _) :-
	error(system_error(not_supported)).


set_stream_position(Handle, _) :-
	var(Handle),
	error(instantiation_error).

set_stream_position(_, Position) :-
	var(Position),
	error(instantiation_error).

set_stream_position(Handle, _) :-
	\+ integer(Handle),
	\+ atom(Handle),
	error(domain_error(stream_or_alias, Handle)).

set_stream_position(_, Position) :-
	Position \= position(_,_,_),
	error(domain_error(stream_position, Position)).

set_stream_position(Handle, _) :-
	check_handle(Handle, Handle1),
	current_stream(Handle),
	error(permission_error(reposition, stream, Handle)).

set_stream_position(Handle, _) :-
	error(existence_error(stream,Handle)).


set_stream(Handle, Prop):-
	check_handle(Handle, Handle1),
	set_stream_property(Handle, Prop).


check_read_term_options(Options, _):-
	var(Options),
    error(instantiation_error).

check_read_term_options([], _) :-
	!.

check_read_term_options([Option| Options], OriginalOptions):-
	!,
	(	var(Option) ->
		error(instantiation_error)
	;	valid_read_term_option(Option) ->
		check_read_term_options(Options, OriginalOptions)
	;	error(domain_error(read_option,Option))
	).

check_read_term_options(_, OriginalOptions):-
	error(type_error(list,OriginalOptions)).


read_term_options(_Handle, _Term, V):-
	var(V),
    error(instantiation_error).

read_term_options(_Handle, _Term, []):-!.


read_term_options(Handle, Term, [Opt|Options]):-
    check_handle(Handle, Handle1),
    read_term_option(Handle, Term, Opt),
    !,
    read_term_options(Handle, Term, Options).

read_term_options(Handle, Term, [_Opt|Options]):-
    read_term_options(Handle, Term, Options),
    !.

% ISO options
valid_read_term_option(variables(_)).
valid_read_term_option(variable_names(_)).
valid_read_term_option(singletons(_)).
% others
valid_read_term_option(module(_)).
valid_read_term_option(syntax_errors(_)).
valid_read_term_option(line_counts(_,_)).
%valid_read_term_option(term_position(_)).

%option(Handle, Term, backquoted_string(false)).
%option(Handle, Term, character_escapes(false)).
read_term_option(Handle, Module:Term, module(Module)).
read_term_option(Handle, Term, module(user)).
read_term_option(Handle, Term, variables(X)):-
    free_variables(Term, X),
    !.
read_term_option(Handle, Term, variables([])).
%read_term_option(Handle, Term, variable_names([])).
%read_term_option(Handle, Term, singletons([])).
read_term_option(Handle, Term, syntax_errors('error')).
%read_term_option(Handle, Term, double_quotes(false)).
%read_term_option(Handle, Term, term_position(0)).
%read_term_option(Handle, Term, subterm_positions(_)).
read_term_option(_, _, _).


check_write_term_options(Options, _):-
	var(Options),
    error(instantiation_error).

check_write_term_options([], _) :-
	!.

check_write_term_options([Option| Options], OriginalOptions):-
	!,
	(	var(Option) ->
		error(instantiation_error)
	;	valid_write_term_option(Option) ->
		check_write_term_options(Options, OriginalOptions)
	;	error(domain_error(write_option,Option))
	).

check_write_term_options(_, OriginalOptions):-
	error(type_error(list,OriginalOptions)).


write_term_options(_Handle, _Term, V):-
	var(V),
    error(instantiation_error).

write_term_options(_Handle, _Term, []):-!.


write_term_options(Handle, Term, [Opt|Options]):-
    check_handle(Handle, Handle1),
    write_term_option(Handle, Term, Opt),
    !,
    write_term_options(Handle, Term, Options).

write_term_options(Handle, Term, [_Opt|Options]):-
    write_term_options(Handle, Term, Options),
    !.

% ISO options
valid_write_term_option(quoted(_)).
valid_write_term_option(ignore_ops(_)).
valid_write_term_option(numbervars(_)).
% others
valid_write_term_option(portray(_)).
valid_write_term_option(backquoted_string(_)).
valid_write_term_option(character_escapes(_)).
valid_write_term_option(max_depth(_)).

%write_term_option(Handle, Term, quoted(false)).
%write_term_option(Handle, Term, backquoted_string(false)).
%write_term_option(Handle, Term, character_escapes(false)).
%write_term_option(Handle, Term, ignore_ops(true)).
%write_term_option(Handle, Term, numbervars(false)).
%write_term_option(Handle, Term, portray(false)).
%write_term_option(Handle, Term, max_depth(0)).
write_term_option(_, _, _).


% user stream properties
%:- jipxio:set_stream_properties(user_output, [mode(write), output, alias(user_output), file_name(user_output), eof_action(eof_code), type(text), reposition(false)]).
%:- jipxio:set_stream_properties(user_error, [mode(write), output, alias(user_error), file_name(user_error), eof_action(eof_code), type(text), reposition(false)]).
%:- jipxio:set_stream_properties(user_input, [mode(read), input, alias(user_input), file_name(user_input), eof_action(eof_code), type(text), reposition(false)]).

format(Stream, Format, Arguments) :-
	atom_codes(Format, Codes),
	format_(Codes, Stream, Arguments).

format_([], _, _).
format_([0'~, Spec| Codes], Stream, Arguments) :-
	!,
	format_spec_(Spec, Stream, Arguments, RemainingArguments),
	format_(Codes, Stream, RemainingArguments).
format_([Code| Codes], Stream, Arguments) :-
	'$put_code'(Stream, Code),
	format_(Codes, Stream, Arguments).

format_spec_(0'a, Stream, [Argument| Arguments], Arguments) :-
%	atom(Argument),
	'$write'(Stream, Argument).
format_spec_(0'c, Stream, [Argument| Arguments], Arguments) :-
	'$put_code'(Stream, Argument).
format_spec_(0's, Stream, [Argument| Arguments], Arguments) :-
	atom_codes(Atom, Argument),
	'$write'(Stream, Atom).
format_spec_(0'w, Stream, [Argument| Arguments], Arguments) :-
	'$write'(Stream, Argument).
format_spec_(0'q, Stream, [Argument| Arguments], Arguments) :-
	'$writeq'(Stream, Argument).
format_spec_(0'k, Stream, [Argument| Arguments], Arguments) :-
	'$write_canonical'(Stream, Argument).
format_spec_(0'd, Stream, [Argument| Arguments], Arguments) :-
	'$write'(Stream, Argument).
format_spec_(0'D, Stream, [Argument| Arguments], Arguments) :-
	'$write'(Stream, Argument).
format_spec_(0'f, Stream, [Argument| Arguments], Arguments) :-
	'$write'(Stream, Argument).
format_spec_(0'g, Stream, [Argument| Arguments], Arguments) :-
	'$write'(Stream, Argument).
format_spec_(0'G, Stream, [Argument| Arguments], Arguments) :-
	'$write'(Stream, Argument).
format_spec_(0'i, _, [_| Arguments], Arguments).
format_spec_(0'n, Stream, Arguments, Arguments) :-
	'$nl'(Stream).
format_spec_(0'~, Stream, Arguments, Arguments) :-
	'$put_code'(Stream, 0'~).


% format(+character_code_list_or_atom, +list) -- built-in

format(Format, Arguments) :-
	'$current_output'(Stream),
	format(Stream, Format, Arguments).
